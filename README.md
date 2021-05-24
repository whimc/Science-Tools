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
The config file can be found under `/plugins/WHIMC-ScienceTools/config.yml`. Use `/sciencetools reload` whenever you change the config.

### Unit Conversions
Unit conversions config will look like this:

```yaml
conversions:
  unit_name:
    expression: "{VAL} rest_of_formula"
    unit: "unit_name"
```

Example:
```yaml
conversions:
  fahrenheit: # Celcius -> Fahrenheit
    expression: '({VAL} * 9.0 / 5.0) + 32.0'
    unit: °F
  feet: # Meters -> Feet
    expression: '{VAL} * 3.28084'
    unit: ft
```

#### Placeholders

| Placeholder   | Description                       |
|---------------|-----------------------------------|
|`{VAL}`        | The value that is being converted |

### Science Tools
Science tool config will look like this:
```yaml
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

Example:
```yaml
tools:
  PRESSURE:
    default-expression: "101.3"
    unit: "kPa"
    conversions:
      - psi
    worlds:
      LunarCrater: "0"
    regions:
      moon_main_dome: "101.3 + rand(-0.01, 0.03)"
      newhouse1: "101.3"
      airlockleak: "0"
  RADIATION:
    default-expression: "2.4"
    unit: "mSv"
    worlds:
      LunarCrater: "380"
      Gliese: "42"
```

Region names are defined using [WorldGuard](https://worldguard.enginehub.org/en/latest/regions/commands/).

#### Placeholders

| Placeholder   | Description                       |
|---------------|-----------------------------------|
|`{Y}`          | The player's Y position           |

### Validation
Validation config will look like this:
```yaml
validation:
  tolerance: error value
  timeout: time (in seconds) until timeout
  messages:
    prompt:
      all:
        - '&8> The message to send'
        - '&8> The next line of the message to send'
      TOOLNAME:
        - '&8> The message to send'
    timeout:
      all:
        - '&8> The message to send'
      TOOLNAME:
        - '&8> The message to send'
    no-number:
      all:
        - '&8> The message to send'
      TOOLNAME:
        - '&8> The message to send'
    found-number:
      all: []
    success:
      all:
        - '&8> The message to send'
      TOOLNAME:
        - '&8> The message to send'
    failure:
      all:
        - '&8> The message to send'
      TOOLNAME:
        - '&8> The message to send'
  commands:
    prompt:
      all: []
    timeout:
      all: []
    no-number:
      all: []
    found-number:
      all: []
    success:
      all: []
      TOOLNAME:
        - "The action"
```

Example Using [Quests](https://github.com/PikaMug/Quests):
```yaml
validation:
  tolerance: 1.0
  timeout: 30
  messages:
    prompt:
      all:
        - '&8>'
        - '&8> &7&lData Entry Computer &7&o(v2.0)'
        - '&8>'
        - '&8>   &7Current {TOOL}: &8Unknown'
        - '&8>'
        - '&8>   &7Please type the {TOOL} value you just measured:'
        - '&8>'
    timeout:
      all:
        - '&8>'
        - '&8> &7Computer left idle.'
        - '&8> &7Logging off... Goodbye!'
        - '&8>'
    no-number:
      all:
        - '&8> &cNo numbers detected!'
        - '&8> &7Click the computer to try again!'
    found-number:
      all: []
    success:
      all:
        - '&8> &7Value updated!'
        - '&8> &7Current {TOOL}: &a{VAL}{UNIT}'
    failure:
      all:
        - '&8> &e{VAL}{UNIT}&7 does not match the range of possible data.'
        - '&8> &7Click the computer to try again!'
      TEMPERATURE:
        - "&8>   &7If you're stuck, talk to &fMisavo&7 again!"
      PRESSURE:
        - "&8>   &7If you're stuck, talk to &fHarlem&7 again!"
      WIND:
        - "&8>   &7If you're stuck, talk to &fHuxley&7 again!"
      OXYGEN:
        - "&8>   &7If you're stuck, talk to &fOlivia&7 again!"
  commands:
    prompt:
      all: []
    timeout:
      all: []
    no-number:
      all: []
    found-number:
      all: []
    success:
      all: []
      ALTITUDE:
      - "questadmin nextstage {PLAYER} Ice on Fire!"
      TEMPERATURE:
      - "questadmin nextstage {PLAYER} What's Cooler Than Being Cool?"
      PRESSURE:
      - 'questadmin nextstage {PLAYER} Feeling the Pressure'
      WIND:
      - 'questadmin nextstage {PLAYER} Not-So-Solar-Wind'
      OXYGEN:
      - 'questadmin nextstage {PLAYER} A Breath of Fresh Air'
      RADIATION:
      - 'questadmin nextstage {PLAYER} Seas of Lava?'
```

#### Placeholders

| Placeholder   | Description                       |
|---------------|-----------------------------------|
|`{TOOL}`       | The current science tool          |
|`{VAL}`        | The provided value                |
|`{UNIT}`       | The current science tool's units  |
|`{PLAYER}`     | The target player                 |

---

## Commands
| Command                     | Description                   |
|-----------------------------|-------------------------------|
| `/sciencetools`             | Display command help          |
| `/sciencetools validate <tool> <player>`| Take the value of the tool at the target player's current location |
| `/sciencetools validate <tool> <player> <world> <x> <y> <z>`| Take the value of the tool at the provided location |
| `/sciencetools reload `     | Reload the plugin's config    |
| `/sciencetools js`          | Run interpreted JavaScript    |

&nbsp;

Using `/sciencetools validate OXYGEN MyName` when standing on LunarCrater (outdoors)
on our server will open a data entry computer prompt in the chat that accepts a
value (input by typing a number in the chat). It will want a value of 0.0% since
the oxygen levels on the moon are at 0.0%.

Using `/sciencetools validate PRESSURE MyName LunarCrater 40 22 37` 
on our server will open a data entry computer prompt in the chat that accepts a
value (input by typing a number in the chat). It will want a value 101.30kPa since
the specified coordinates are in a specific building with a different pressure
than the outside (0kPa).

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