# core
> The main beansmc server core custom plugin.

---

## Building
To build `beansmc/core` to use in your own server we recommend using [IntelliJ IDEA Community Edition]() to build the `core-{VERSION}.jar` file through Maven.

1. Clone the repo using either: `gh repo clone beansmc/core` or `git clone https://github.com/beansmc/core`
2. Open IntelliJ IDEA Community Edition in the core folder we just cloned
3. Click on the hamburger icon (4 lines on top of each other) in the top left
4. Click on Build -> Build Project
5. Wait and then once that is done click on the Maven symbol (a lowercase `m`) on the right bar
6. Select core and then press the green arrow/play button to build
7. Wait a minute...
8. The `target` folder will be created and the `core-{VERSION}.jar` file will be created inside it

To play with this `core-{VERSION}.jar` file drag it into a [Paper](https://papermc.io/software/paper/) server's `plugins` folder, run the server and join the server through Minecraft.

---

## Commands
- `/core` - The main command.
```
<br><br><shadow:black>                     <dark_gray>---</dark_gray> <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> <dark_gray>---</dark_gray> </shadow><br>       The main <shadow:black><gradient:#F95A22:#FFA200>Beans</gradient><gray>-</gray><color:#D4DDE0>m</color><color:#dead50>c</color></shadow> server <shadow:black><gradient:#F70E4D:#F12760>Core</gradient></shadow> plugin.<br>                    <shadow:black> <dark_gray>------------</dark_gray> </shadow><br><shadow:black>                   <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> help <gray>{page} </shadow><br><shadow:black>                <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> player <gray>{player} </shadow><br><br>
```

- `/core help {page}` - The help pages.
```
<br><br><shadow:black>              <dark_gray>---</dark_gray> <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> help <gray>{page}</gray> <dark_gray>---</dark_gray> </shadow><br><br><shadow:black>                <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> player <gray>{player} </shadow><br>         Shows info about a specific player.<br>  <br>           <shadow:black> <dark_gray>----------------------</dark_gray> </shadow><br><br><br>
```

- `/core player {player}` - Shows info about a specific player.
```
<br><br><shadow:black>           <dark_gray>---</dark_gray> <gray>/</gray><gradient:#F70E4D:#F12760>Core</gradient> player <gray>{player}</gray> <dark_gray>---</dark_gray> </shadow><br><shadow:black>                     <yellow><player_name></yellow> </shadow><br><br>                     <player_hp><red>♥</red><br>                  <player_location><blue>✈</blue><br><br>         <shadow:black> <dark_gray>---------------------------</dark_gray> </shadow><br><br><br>
```

