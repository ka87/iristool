IrisTool (c) 2015-2021 Andras Kovacs (kandras87@gmail.com)

You can use this tool to open Beyond Good & Evil's data file (.bf), and view it's textures, levels,
play sounds & music (only PC version), etc. It can play model animations too.

IrisTool can not alter game data files, nor extract meshes, textures, sounds, etc.

All versions of the game (original: PC/GC/PS2/XBOX, HD: PS3/XBOX360) are supported 
(two PC demos and full games were tested).

Requirements:
- Java JRE (Oracle 1.8 is preferred)
- Graphics card with OpenGL 4 support.
- At least 2GB of free RAM.

Usage:
Unzip zip contents to a directory, and click on IrisTool.jar to start application.
(Other method: Inside the directory where you extracted zip contents issue this command: java -jar IrisTool.jar)
If you have Java installed, IrisTool will start, and ask you to select bf/spe files to open.

Changelog (from v0.26)
- Fixed parsing error when opening the new uplay/stream version of the game
- Position mode handling selection in animation player (No position update/Global only/All position)

Changelog (from v0.25)
- Collision map & instance debug display
- Fixed lights distance calculation
- More modifier support: Snake, Lazy (makes animation more loook like as in original game), 
  and Inverse Kinematics (cranes movement and calculation)
- Implemented "object look at camera"-type rendering (eg. fixes pearl's halo display)
- Fixed particle generator generated particles misplacement
- Implemented diffuse UV mapping
- Implemented planar UV mapping for remaining matrix types (viewMatrix, globalMatrix) (fixed BGE logo display)
- Added Goto Game Object possibility in Goto Position dialog

Changelog (from v0.24)
- Fixed aspect ratio 
- Usable FPS-like camera
- Realtime lights support
- Fog support
- Sprite generator support
- Particle engine
- Mesh wave modifier support
- File list searchable, Game object key & name search 

Limitations:
- only these GAO modifier are supported: FakeLookAtCamera, InverseKinematics, Lazy, 
  MeshWave, Morph, Snake, SpriteGen
- no scripting and cutscene play
- no collision/physics (only can display collision objects)
- no procedural water

Uses:
- MiniLZO Java port (Mahadevan Gorti Surya Srinivasa, sgorti@gmail.com)
- Jogamp for OpenGL (https://jogamp.org/)
- HexLib (http://hexedit-lib.sourceforge.net/)
- Microsoft ADPCM Decoder (Ethan Lee, http://www.flibitijibibo.com/)

Feel free to use this tool, and if you have any questions, use kandras87@gmail.com email address.