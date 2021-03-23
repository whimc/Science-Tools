# WHIMC-ScienceTools
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/whimc/Science-Tools?label=download&logo=github)](https://github.com/whimc/Science-Tools/releases/latest)

ScienceTools is a Minecraft plugin to simulate values for scientific tools. This plugin uses [WorldGuard](https://worldguard.enginehub.org/en/latest/developer/dependency/) to define regions in which to set scientific values. To set values, edit the config file.

---

## Building
Build the source with Maven:
```
$ mvn install
```

---

## Configuration
The config file can be found under /plugins/WHIMC-ScienceTools/config.yml. Use `/sciencetools reload` whenever you change the config.

Unit conversions config will look like this:
```
conversions:
  unit_name:
    expression: "{VAL} rest_of_formula"
    unit: "unit_name"
```

Science tool config will look like this:
```
tools:
  TOOL_NAME:
    default-expression: "value"
    unit: "unit_name"
    conversions:
      - conversion_name
    worlds:
      WorldName: "value"
    regions:
      region_name: "value"
```
Region names are defined using WorldGuard.

---

## Commands
| Command                   | Description                   |
| ------------------------- | ----------------------------- |
| /sciencetools             | Display command help          |
| /sciencetools validate    | Have a user validate a tool   |
| /sciencetools reload      | Reload the plugin's config    |
| /sciencetools js          | Run interpreted JavaScript    |

&nbsp;

`/[tool_name]` will measure the current value in the region that the player is standing in.

We currently support:
- altitude
- oxygen
- pressure
- radiation
- temperature
- wind

We are planning on adding:
- atmospheric makeup

---

## Dependencies
- [WorldGuard](https://worldguard.enginehub.org/en/latest/developer/dependency/)