![Logo](https://mgrab.dev/images/SteamSaleScraper/SteamScraperLogo.svg)
<p align="center">A tool for scraping and viewing Steam game listings currently on sale</p>

## Content
- [Features](#features)
- [Tech Stack](#tech-stack)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Roadmap](#roadmap)

## Features
- User-friendly GUI
- Real-time Steam on-sale game listing scraping
- View details (Cover / Description / Release Date / Rating) of scraped game listings
- View Steam pages of scraped game listings
- View the discount percentages of game listings  
  (recently partially restricted functionality on the Steam platform, here the percentages are available for all listings)
- Sort scraped game listings by Title / Price / Original Price / Discount
- Filter game listings by game genre (e.g. Horror / Action / Global Top Sellers (all) and other Steam's 'tags')
- Fullscreen mode
- Available on Windows / Mac / Linux

## Tech Stack
- The application is fully written in Java
- Additional technologies used: Jsoup, Gson, JavaFX & SceneBuilder

## System Requirements
- Java Runtime Environment OR SDK installed (other necessary things are bundled within the application's main JAR file)
- Windows / Mac / Linux OS

## Installation
1. Head into the "Production" directory of the project and download the directory corresponding to your OS.  

   Within the directories there are: the Application (SteamSaleScraper.jar), Launcher (.bat file for Windows / .command file for Mac & Linux) that executes the .jar file for you,
   and the app icon.
   
3. After downloading the directory corresponding to your OS, place it somewhere you usually install apps on your PC.
   
4. Create a shortcut (alias on Mac & Linux) of the Launcher file. You use this shortcut to launch the app.
   
   If you want to, you can change the shortcut's icon for the app icon supplied within the directory. You can move the shortcut anywhere on your PC.

## Roadmap
- Search for and obliterate bugs :)
- Add new functionalities (reach out to me with cool ideas!)
- Optimize scraping speed
- Develop the GUI further
- Maintain the application according to Steam's future game listing UI updates
