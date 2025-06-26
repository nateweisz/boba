# Components

The way that boba works is that we have a single view method that returns the entire frame. 
For components, there is going to be a seperate interface used to render these, a child of the generic Program entrypoint.
We will start by rendering the parent div, which every div will have to be under. This can be styled, given hard coded sizing but cannot exceed the width of the 
terminal size. If there is a hard coded size for it we will display an error across the entire screen. On our first passthrough of a component
we will calculate the sizing (rows and columns). Our second passthrough will be rendering it, after rendering the outside. 

Send to ethan later:
Where do you think we should handle the application of properties (CSS Styles). Right now I am structuring
it similar to HTML with a parent div class that holds all the children. Properties can be applied to anything, although
you will be able to create component specific properties. I want some input on whether we should have a general properties be
functional in a sense vs having them just be used as info/marker classes and have logic for rendering them inside Div. Div has
a method that returns a String[] containing the whole content of the Div which we will do a double passover to calculate sizing and then render.

You can view the progress I have made [here](http://github.com/pot/boba/tree/feat/components/components-core/src/main/java/dev/weisz/components/properties)