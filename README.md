# ComputationalGeometry
BRNN: Bichromatic Reverse Nearest Neighbor
------------------------------------------------------------------------------------------------------------------------
This is the project I am undergoing in 2014 with Dr.Langetepe at the Universtiy of Bonn, Germany. The aim of this project is to implement the algorithms which were introduced in the paper "Facility location problems in the plane based on reverse nearest neighbor queries".

Motivation of probem:
Imagine a city with customers (red points) and grocery stores(blue points).
Now, we would like to build a new grocery store who can attract the maximum possible amout of customers.
Question: Where should such a new grocery store be placed?
Note: We assume that every customers goes to the nearest grocery store.

Best location means the location where as much customers can be covered, also known as MAXCOV. 
This problem can be solved in O(n²), wheres as n is the number of customers.

Due to copyright I was not able to publish all the code here but you can still try out the project by downloadig the .jar-file. This is an applet of the final project.
Below is a description on how to use the applet.

If there are any more questions, please be free to contact me.


How to use the applet
------------------------------------------------

Mouse functionalities

The left mouse click can be used to add new points and the right mouse to delete a point.

Bottom of panel

On the bottom of the panel there are two radiobuttons named 'Customers' and 'Facilities'. By choosing one or the other it is possible to either add a new customer or a new facility respectively. By selecting the checkboxes, one can show the voronoi diagram, disks, dual graph, labels and/or the new facility's location. Note that for selecting the label checkbox, one first needs to check the dual graph.

Top of panel

On the top of the panel there is an on/off-button which lets the user decide whether to run the algorithm or not. Other than that there are three more buttons. The first one, namely 'Animation mode', lets the user to go through the example he has created step by step. The second one, namely 'Display', is connected to the combo box next to it. There are three precreated examples for the user to try out. In order to show those examples, one simply picks the example in the combo box and then presses the display button. The last button from the right contains a trash symbol which is for clearing the whole screen again.

Animation mode

When clicking on the 'Animation mode'-button on the top of the panel, one gets to a new screen. In 'Animation mode' the top panel gives the user the possibility to play/pause the animation. Once the play/pause button is pressed one can make it slower or faster by the left or right button next to the play/pause-button respectively. To show the animation from the beginning again, one can simply press the fourth button, namely 'Reset'. For going back to the home screen to enter new points or set a new example the last button can be pressed. Note that no points can be deleted nor added while in Animation mode. It is mandatory to go back to the home screen for doing so. While the animation runs, the user can see what steps of the algorithm is visualized by looking at the description in the text field on the bottom of the panel in animation mode.
BRNN Applet




Bibliography: Sergio Cabello, José Miguel Diáz-Bánez, Stefan Langerman, Carlos Seara, Inmaculada Venturaán. Bichromatic Reverse Nearest Neighbor. August 14, 2002.

For interested ones, the paper can be read here: http://www.researchgate.net/publication/220289786_Facility_location_problems_in_the_plane_based_on_reverse_nearest_neighbor_queries
