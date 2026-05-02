# Tripwire Hook Embargo

![LOGO](https://i.imgur.com/iafb3N2.png)

Paper plugin for Minecraft `1.21.11` that removes the tripwire hook from fletcher villager trades.

This is a novel solution to redstone contraptions that dupe tripwire hooks, simply remove its usefulness in trade! Set an embargo!
## Features

- Removes the buy trade from all currently loaded fletcher villagers
- Removes the buy trade from newly generated and newly leveled fletcher villagers
- Lets admins view or set the embargo state with one command

## Command

`/hookembargo`

- No argument: shows current status
- `true`: enables the embargo and sanitizes loaded fletchers
- `false`: disables the embargo

Required permission: `tripwire.admin`

## Build

```powershell
.\gradlew.bat build
```

Built jar output:

`build/libs/Tripwire-Hook-Embargo-1.0.0.jar`

## Notes

- Default config is `enabled: true`
- Disabling the plugin stops future removals, but it does not restore trades already removed from villagers
- Paper requires a space-free internal plugin name, so the server data folder is `plugins/TripwireHookEmbargo/`

## Author

- GitHub: [cev-api](https://github.com/cev-api)
