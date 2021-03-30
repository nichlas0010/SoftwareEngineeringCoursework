## TXT Files
All objects in the game have a .txt file. This file contains the name of the object.
If you wish to edit this name, edit the name in the file, not the name of the file.

The board.txt file is different. It contains the layout of the board. Each character
in the file is equivalent to one tile, although rooms are handled specially. 

Below you can see a legend of what each character corresponds to:

t - Tile that players can walk on

space - empty, no tile. Simply there as a structural guide

\> - door that can be entered from the left

v - door that can be entered from above

< - door that can be entered from the right

^ - door that can be entered from below

s - Study

l - Library

a - Ballroom

i - Billiards room

c - Conservatory

k - Kitchen

d - Dining room

o - Lounge

h - Hall

w - Start for Mrs. White, otherwise a normal tile

g - Start for Mr. Green, otherwise a normal tile

p - Start for Mrs. Peacock, otherwise a normal tile

r - Start for Professor Plum, otherwise a normal tile

m - Start for Miss Scarlet, otherwise a normal tile

u - Start for Colonel Mustard, otherwise a normal tile.

## PNG files

Each object also has a .png file, this is the image that represents said object.
You can edit that image to your heart's content, and the changes will show up in-game.

The same holds true for the board.png image, although you should be more careful there.
The board.png file isn't used to determine where tiles are, so if you aren't careful
you might cause a mis-match between the tiles and the graphical representation.

As a rule of thumb, each tile in-game should correspond to 32x32 pixels in the image.
So a 4x4 board would be 128x128

## I want to mod, what do I do?

Depends on what you want to modify. If you want to modify the characters/weapons
you simply need to open up the corresponding .txt and .png files, and edit them.

If you want to modify the rooms/board, it gets more complicated:

The .png/.txt files for each individual rooms, are only used for the cards.
So, if you intend to mod the name of a room, make sure you also edit the board.png
and the corresponding room.png to reflect your change.

If you want to edit your board, here's a short guide:

Tiles! The main chunk of the board is made up of tiles. Tiles are normal walkable
tiles, that can only contain one player at a time.

Rooms and doors, how do they work? Essentially, the four door characters function
as normal tiles, except they funnel players into the rooms they're next to.
In order to make a door go into a room, simply just place the door next to the room.

Secret passages work by simply connecting two rooms. If two rooms have characters next
to each other, the player will be able to move between them. as such, you can connect two
rooms on either side of the map, by simply putting one character inside the other room.

Be aware that the game doesn't sanity check your board. If you place a door between 
two rooms, you'll be creating a way for players to get stuck, as they won't be able to move.

