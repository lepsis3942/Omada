# Omada Interview App

Hi! 👋 I'm Christopher Gonzalez and this is my submission for Omada Health

My approach was to balance getting this done in a timely manner while showing how you might build a small app, with the intention to scale it up

If you have any issues running the code or any questions, please do reach out to me at cjgonz3942@gmail.com

## Getting Running
- To run, ensure you create a 'secrets.properties' in the root of the application with the contents (substituting in your API key):

  FLICKR_API_KEY=XXXXXXXXX

- My development was done on a Macbook Pro with an M2, in case there is any weirdness running this on a Windows/Linux machine
- For simplicity and development speed I set the target and min SDK to 33, in a real app care would be taken to support older versions of Android

## Approach

- Jetpack Compose is used for UI
- Hilt is used for dependency injection
- Coroutines for asynchronous work
    - StateFlow used for communication in a uni-directional way with UI
- Ktor used for networking
- Coil used for image loading

I wanted to demonstrate modularizing an app but I usually would advocate for a Domain module of pure Kotlin classes to define a set of common models across that app all modules could communicate with> for brevity in this exercise I omitted that

It is broken into the following modules:
- App
    - Deployable Android app
    - Contains only UI related code
    - For simplicity I let the viewmodel handle all of the logic around managing state for when to grab new pages, in a real app ideally this business layer could offload some of that to keep the VM simpler
- Data
    - Contains logic related to obtaining data from external and internal (database, files, etc) sources
    - In this example the repository layer is fairly thin. It's doing a bit of conversion logic. It could be extended to cache data for sharing across the app, especially if hand-rolling pagination is a direction desired for a real production app. The repository could do a lot more heavy lifting
- Network
    - Handles all logic and serialization related to interacting with remote servers

## Unit Testing
- A small sampling of unit testing of the viewmodel was included. Certainly would cover more in a real app. Services and repositories should be tested as well, for brevity-sake on this take home test I only added VM tests

## Things to note
- I really wanted to get to implementing shared element transitions [https://developer.android.com/develop/ui/compose/animation/shared-elements](https://developer.android.com/develop/ui/compose/animation/shared-elements)] but ran out of time that I have to work on this. So I went with modal bottom sheet for detail view instead
- Due to the nature of users uploading content to the service continuously subsequent calls to the Recents API can return duplicate images
  - For simplicity of the assignment I did not handle cleaning the data and de-duplicating the responses
  - In a real app your repository is probably caching data and should only expose clean, good data to the view layer
