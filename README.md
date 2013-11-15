Glasscutter is a program that slices color 3D objects into a set of
flat slices.

**Brief Rundown Of The Program**
[Here's the Glasscutter UI](http://imgur.com/3wQSfQg)
There are three main sections of the UI:

**The box dimensions**

When all the slices are stacked up, they will form a box of this
dimension.

**Slicing axis/slice thickness**

The slices will be normal to the slicing axis.  The slices have a
certain thickness -- this is the thickness of the material that the
slices will be printed on.

There is a number of microslices that make up each slice.  These
microslices will all be printed together onto their slice.  Think of
them like contour lines on a topological map.  The closer the contour
lines get, the more detail they can represent on the map.

The next version of Glasscutter, currently in beta, replaces
microslices by drawing a polygon onto the slice if the triangle is
flat enough that it lies within the thickness of a slice.

**Output Options**

Glasscutter currently supports two export formats:  png and pdf.  

The options at the top are all for the pdf export -- output page
dimensions, columns, rows and margin.  The pdf will contain a
set of dimensionally accurate pages that can be directly printed and cut.
An example pdf export is [here](http://imgur.com/Lzw1tOf), sliced from
a colored version of [this thingiverse thing](http://www.thingiverse.com/thing:32477)

Page dimensions set the size of the page, rows and margins set the
number of slices that go on a page, spacing is the distance between
the slices and margin is the margin between the slices and the edge of
the page.

The bottom options are for the png export.  The resolution sets, well,
the resolution of the png in dpi.  'Overlap' prints microslices from
the neighboring slices onto the current slice.  The number is from
0-1, where a 0 indicates no overlap, a 0.5 indicates a 50% overlap and
a 1 indicates a 100% overlap.

Finally, in the center, there are two checkboxes to set pdf and png
exports.  If no boxes are checked, the program will just slice the
model and show a preview, but not export anything.  If either box is
checked, the program will export that format.

**Language**

Glasscutter is written in Processing, which is based on Java.  The
Processing environment is open-source and free for download from
http://www.processing.org 

**Dependencies:**

Glasscutter requires the ControlP5 library for graphical user
interfaces, available for download [here.](http://www.sojamo.de/libraries/controlP5/)
To install, download the library from the above website and unzip it.
There's a folder that contains your Processing sketches -- for mac
users, the default location is in ~/Documents/Processing, and for
windows users it's in My Documents/Processing.  Inside that folder,
there's another folder called 'libraries'.  Drag the unzipped
directory that you downloaded into the libraries folder and restart
processing.  You're good to go!

**License**

This work is licensed under the Creative Commons Attribution-ShareAlike
3.0 Unported License.
The summary and full text of the license are viewable [here](http://creativecommons.org/licenses/by-sa/3.0/deed.en_US)


This version of Glasscutter can only slice ascii-formatted,
vertex-colored PLY files.  Fortunately, the open-source software
[Meshlab](http://meshlab.sourceforge.net/) can convert just about any 3D
file format into ascii-formatted, vertex-colored PLYs.  Here's how:

**Vertex Coloring**

There are two ways to color 3D files:
vertex coloring and texture-mapping.  In texture mapping, there is an
image file that contains the flattened "skin" of the 3D object.  Each
vertex in the mesh contains a co-ordinate from the texture that
describes how to wrap the skin around the mesh.  Glasscutter doesn't
handle this, yet.  If you want to help make this happen, you would
be a rock star hero.

Vertex coloring is the other approach.  In this approach, each vertex
in the mesh has a certain color.  When the renderer draws the
triangles in the mesh, it'll color each triangle with a linear
gradient between the vertex colors along the triangle's surface.  This
is a much simpler way to color a mesh, but it has a limitation:  the
resolution of a vertex-colored mesh is based on the number of vertices
in the mesh.  You can add more vertices to increase the resolution,
but it makes the 3D file larger and more difficult to handle.  Texture
mapping scales much better.  

You can use meshlab to convert a texture-mapped mesh to a
vertex-colored mesh.  Open the 3D file in Meshlab, and then go to to
Filters->Color Creation and Processing->Transfer Color: Texture to
Vertex.  Give it a second, and your 3D object will now be
vertex-colored.  You'll still have to export the PLY file, though.
Read below to see how.


**Exporting PLYs**

Exporting PLYs is simple, but there are a few things that you have to
get right.  To export a file as a PLY, first open the file in
Meshlab.  Then, go to File->Export Mesh As... and select "Stanford
Polygon File Format (*.ply)" in the "files of type" drop-down menu.
 
[The following dialog box will appear](http://imgur.com/WbxmJFI)

Only click the "Color" box under 'Vert.'  All other checkboxes should
be unclicked, _especially_ the 'Binary Encoding' checkbox --
unchecking this ensures that the PLY file will be in human-readable
ASCII format.  At the bottom left of the checkbox, the radio button
for 'All' should be selected.  
Click 'Save', and you'll have a PLY file that Glasscutter can handle.

