package edu.whimc.sciencetools.utils;

import edu.whimc.sciencetools.models.sciencetool.ScienceTool;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Matches kid spellings of science tool names using prefixes, edit distance, and phonetics.
 */
public final class ToolNameMatcher {

    private static final int MAX_ACCEPT_SCORE = 44;

    private ToolNameMatcher() {
    }

    /**
     * Finds the unique best tool for a typed name, or null if nothing is close enough.
     *
     * @param typed The name the player typed.
     * @param tools All loaded science tools.
     * @return The matching tool, or null.
     */
    public static ScienceTool findBest(String typed, Collection<ScienceTool> tools) {
        List<ScienceTool> matches = findBestMatches(typed, tools);
        return matches.size() == 1 ? matches.get(0) : null;
    }

    /**
     * Finds every tool that shares the best acceptable score for a typed name.
     *
     * @param typed The name the player typed.
     * @param tools All loaded science tools.
     * @return Matching tools, empty if nothing is close enough.
     */
    public static List<ScienceTool> findBestMatches(String typed, Collection<ScienceTool> tools) {
        List<ScienceTool> none = new ArrayList<>();
        if (typed == null || typed.isEmpty() || tools == null || tools.isEmpty()) {
            return none;
        }

        String input = typed.toLowerCase(Locale.ROOT).trim();
        List<ScienceTool> bestTools = new ArrayList<>();
        int bestScore = Integer.MAX_VALUE;

        for (ScienceTool tool : tools) {
            int score = bestScoreForTool(input, tool);
            if (score < 0 || score > MAX_ACCEPT_SCORE) {
                continue;
            }
            if (score < bestScore) {
                bestScore = score;
                bestTools.clear();
                bestTools.add(tool);
            } else if (score == bestScore) {
                bestTools.add(tool);
            }
        }

        return bestTools;
    }

    private static int bestScoreForTool(String input, ScienceTool tool) {
        int best = Integer.MAX_VALUE;
        for (String name : namesOf(tool)) {
            int score = scoreName(input, name);
            if (score >= 0 && score < best) {
                best = score;
            }
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    private static List<String> namesOf(ScienceTool tool) {
        List<String> names = new ArrayList<>();
        names.add(tool.getToolKey());
        if (tool.getAliases() != null) {
            names.addAll(tool.getAliases());
        }
        return names;
    }

    private static int scoreName(String input, String rawName) {
        String name = rawName.toLowerCase(Locale.ROOT).replace('_', ' ').trim();
        String nameKey = lettersOnly(name);
        String inputKey = lettersOnly(input);
        if (nameKey.isEmpty() || inputKey.isEmpty()) {
            return -1;
        }

        if (nameKey.equals(inputKey)) {
            return 0;
        }

        String nameNorm = normalize(nameKey);
        String inputNorm = normalize(inputKey);
        if (nameNorm.equals(inputNorm)) {
            return 5;
        }

        if (inputKey.length() >= 3 && nameKey.startsWith(inputKey)) {
            return 10;
        }

        String nameSkel = skeleton(nameKey);
        String inputSkel = skeleton(inputKey);
        if (nameSkel.equals(inputSkel) && inputSkel.length() >= 4) {
            return 20;
        }

        int skelDist = levenshtein(nameSkel, inputSkel);
        if (skelDist == 1 && Math.min(nameSkel.length(), inputSkel.length()) >= 4) {
            return 30;
        }

        int normDist = levenshtein(nameNorm, inputNorm);
        int maxNorm = Math.max(2, Math.min(3, inputNorm.length() / 3));
        if (normDist > 0 && normDist <= maxNorm && inputNorm.length() >= 5) {
            return 40 + normDist;
        }

        int rawDist = levenshtein(nameKey, inputKey);
        if (rawDist > 0 && rawDist <= 2 && inputKey.length() >= 6) {
            return 42 + rawDist;
        }

        return -1;
    }

    static String lettersOnly(String value) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = Character.toLowerCase(value.charAt(i));
            if (ch >= 'a' && ch <= 'z') {
                out.append(ch);
            }
        }
        return out.toString();
    }

    static String normalize(String letters) {
        String s = letters;
        s = s.replace("ph", "f");
        s = s.replace("gh", "");
        s = s.replace("ck", "k");
        s = s.replace("qu", "k");
        s = s.replace("q", "k");
        s = s.replace("x", "ks");
        s = s.replace("ch", "k");
        s = s.replace("sh", "s");
        s = s.replace("th", "t");
        s = s.replace("z", "s");
        s = replaceSoftC(s);
        return collapseRepeats(s);
    }

    static String skeleton(String letters) {
        String normalized = normalize(letters);
        if (normalized.isEmpty()) {
            return normalized;
        }
        StringBuilder out = new StringBuilder();
        out.append(normalized.charAt(0));
        for (int i = 1; i < normalized.length(); i++) {
            char ch = normalized.charAt(i);
            if ("aeiouy".indexOf(ch) < 0) {
                out.append(ch);
            }
        }
        return out.toString();
    }

    private static String replaceSoftC(String value) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == 'c') {
                char next = i + 1 < value.length() ? value.charAt(i + 1) : '\0';
                out.append(next == 'e' || next == 'i' || next == 'y' ? 's' : 'k');
            } else {
                out.append(ch);
            }
        }
        return out.toString();
    }

    private static String collapseRepeats(String value) {
        if (value.isEmpty()) {
            return value;
        }
        StringBuilder out = new StringBuilder();
        char last = 0;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch != last) {
                out.append(ch);
                last = ch;
            }
        }
        return out.toString();
    }

    static int levenshtein(String left, String right) {
        int n = left.length();
        int m = right.length();
        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];
        for (int j = 0; j <= m; j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            for (int j = 1; j <= m; j++) {
                int cost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] swap = prev;
            prev = cur;
            cur = swap;
        }
        return prev[m];
    }
}
