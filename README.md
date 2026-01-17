# Skyblock Remaining (1.8.9)

A personal client-side Skyblock utility mod designed to add quality-of-life features not currently
available in other major mods. The primary use of the Hypixel API is to fetch data to populate a 
custom in-game 'To-Do List,' tracking status for the Composter, Aranya, etc. The mod also includes 
client-side HUD elements like a coordinate display.

---

## Commands

### `/sbr`

Opens the main **Settings Menu**.

* **Toggle Features:** Enable or disable specific trackers (e.g., Composter).
* **Move HUD:** Click "Move HUD Elements" to drag the overlay to a new position on your screen.

### `/sbrapikey <your-key>`

Sets your Hypixel API key and automatically configures your UUID.

* **Usage:** `/sbrapikey 1234-abcd-5678-efgh`
* **Note:** You **must** run this once for the mod to access your Skyblock data. The mod will automatically detect and 
save your Minecraft UUID when you run this command.

---

## Todo
- [x] Todo Item: Composter 
- [ ] Todo Item: Rift Split or Steal
- [ ] Todo Item: Aranya
- [ ] Todo Item: Forge
- [ ] Todo HUD Display
- [x] Config menu
- [ ] Coords HUD Display
- [x] Commands Hotkey with toggle on/off
