# UberGarage Application

This application lets the user temporarily rent out a nearby garage or one which has all the needed requirements.
This was my first "bigger" application that I made for learning purposes and it was presented as a marketing assignment.
Demonstrational/promotional video can be found here:
https://youtu.be/cJXxhEbtkq0?t=35

# Demo
![Demonstrational gif](https://github.com/Rimve/UberGarage/blob/master/app/ubergarageDemo.gif)

# Appliaction features

Add an offer

	Offer can have up to 8 images. If there is no picture a map. location will be set as offers main image. Offer must have:
     -price
     -marked location
     -entered location
     

 - Long press to delete photo
 - Select which options your offer has
 - Filter offers by attributes
 - See your offers
 - Preview offer
 - Preview activity does not show imageView placeHolder if the offer has
   no images
 - Preview individual offer images
 - All around validation throughout the application
 - Uses room local database (can easily be changed to a live database when needed)

There are two database entities:

    - UserEntity (stores user data)
    - OfferEntity (stores offer data)

 - Working login and register
 - Asynchronous image loading in recyclerView (needs to be fixed)
 - There are two recyclerViews (profile and offers)
 - All images have rounded corners (sounds easy to do - not quite though)
 - Dynamically increasing cardView inside a scrollView (broke android
   rules for this one)

Using GoogleMAPS API:

    - Zooms on set offer location in preview
    - Must place a marker on the map while adding an offer

 - Custom button styles
 - Custom switch styles
 - Custom checkbox styles
 - Custom toolbar
 - Custom icons all around the application

In Profile activity a user has the ability to:

    - Edit his offer (all previous data is loaded)
    - Delete his offer (confirmation dialog pops up)
    - While editing offer user can remove unwanted images by holding (confirmation dialog pops up)

 - Double click back to exit application

# *TODOs:*
// TODO: add search bar in the mapViews  
// TODO: add likes  
// TODO: add an option to take a picture with the camera  
// TODO: expand the filter menu  
// TODO: fix add image button  
// TODO: fix asynch tasks  
// TODO: fix image cropping  
// TODO: capitalise first letters
// TODO: revamp the whole design of the main view

