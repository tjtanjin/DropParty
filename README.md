<p align="center">
  <img width=300 src="https://i.imgur.com/IY2eUJx.png" />
</p>
<h1 align="center">DropParty</h1>

<p align="center">
  <a href="https://github.com/tjtanjin/DropParty/actions"> <img src="https://github.com/tjtanjin/DropParty/actions/workflows/maven.yml/badge.svg" /> </a>
</p>

## Table of Contents
* [Introduction](#introduction)
* [Features](#features)
* [Technologies](#technologies)
* [Setup](#setup)
* [Team](#team)
* [Contributing](#contributing)
* [Others](#others)

### Introduction
**DropParty** is a simple plugin for adding a touch of fun to your server in the form of drop 
parties. This is the lite version of [**DropPartyFiesta**](https://www.spigotmc.org/resources/droppartyfiesta.113746/), and thus contains a subset of the premium 
features. With the lite version, you can access the following features:You may download the plugin via the following links:

- [Modrinth](https://modrinth.com/plugin/dropparty)
- [Spigot](https://www.spigotmc.org/resources/dropparty.118050/)

### Features
<p align="center">
  <img src="https://i.imgur.com/PXM05MI.gif" />
  <img src="https://i.imgur.com/IrC84Zd.gif" />
</p>

Some key features provided by this lite version plugin are listed below:
- 2 creative ways to host drop parties
- Throw drop party in chat (players click on a message for items, available only on 1.12+)
- Throw drop party into inventories (items drop into online player inventories)
- 14 configurable options
- Clone drop parties with a command to save time
- In-game GUI editor for easy setup
- Fully customizable GUI
- Fully customizable messages (with options for your own language files!)

The features above are just a glimpse of what the plugin is capable of. More detailed guides and 
example setups can be found in our **[wiki](https://github.com/tjtanjin/DropParty/wiki)**. You may
also refer to the table below for a comparison between the lite ([**DropParty**](https://www.spigotmc.org/resources/dropparty.118050/)) and premium 
([**DropPartyFiesta**](https://www.spigotmc.org/resources/droppartyfiesta.113746/)) version of the plugin:

![drop-party-comparison](https://github.com/user-attachments/assets/23c57104-1c78-4628-b02b-19c1ec475280)


### Technologies
Technologies used by DropParty are as below:
##### Done with:

<p align="center">
  <img height="150" width="150" src="https://brandlogos.net/wp-content/uploads/2013/03/java-eps-vector-logo.png"/>
</p>
<p align="center">
Java
</p>

##### Project Repository
```
https://github.com/tjtanjin/DropParty
```

### Setup
Setting up the DropParty project locally would involve the following steps:
1)  First, `cd` to the directory of where you wish to store the project and fork/clone this repository. An example is provided below:
```
$ cd /home/user/exampleuser/projects/
$ git clone https://github.com/tjtanjin/DropParty.git
```
2) Make any updates/changes you wish to the code. Once ready, you may build the plugin with the following command:
```
mvn clean install
```
If you are satisfied with your work and would like to contribute to the project, feel free to open a pull request! The forking workflow is preferred in this case so if you have the intention to contribute from the get-go, consider forking this repository before you start!

### Team
* [Tan Jin](https://github.com/tjtanjin)

### Contributing
If you have code to contribute to the project, open a pull request from your fork and describe 
clearly the changes and what they are intended to do (enhancement, bug fixes etc). Alternatively,
you may simply raise bugs or suggestions by opening an issue.

Note that as this was my first minecraft plugin, the structure of the codebase leaves more to be
desired. My plan to rewrite the plugin for version 2.0.0 is delayed indefinitely until I am able to
free up more time (or until a volunteer comes along :stuck_out_tongue_closed_eyes:)

### Others
For any questions regarding the project, please reach out for support via **[discord](https://discord.gg/X8VSdZvBQY).**
