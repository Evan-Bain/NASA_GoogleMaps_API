# Nasa-GoogleMaps-Api-Project

This project consists of four main screens incorperated into one activity using the Navigation Compenent provided by Android Jetpack.

The first screen "fragment_home" displays an enter animation using Motion Layout.
The first screen also displays the NASA image of the day from the "planetary apod api" provided by NASA.
After this image has been loaded once, it automatically caches the data into a room database to ensure faster load times on next start.
The first screen includes one button directing the user to "satellite_images_fragment".

The second screen "satellite_image_fragment" is opened from "fragment_home" with a fade animation set to 300 milliseconds.
This screen sorts all image data by year and includes a header above each new year in the list.
The user can either view an image and it's data by clicking a cardView or adding a new image by clicking the "Add" button 
located at the bottom of the screen.

The third screen consists of a Google Map provided by the Google Maps Api. 
This screen asks for user fine or coarse location, but is not required to use the app.
If a user selects a position on the map a marker will appear along with the center of the phone screen shifting to the marker.
Once a marker is selected the user can select the marker's tag which bring up a cardView dialog requesting the user to enter the date
they wish to view the selected location at, along with entering a title for the image.
If the user choices to keep the data and press the "check" button that data entered in will be saved a local database provided by Room.

The fourth screen displays the image of the selected data in a larger frame along with shows extra data about the image including:
location of image, date the image of was added to the database, and the date the image was taken.
