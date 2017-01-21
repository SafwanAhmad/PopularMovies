#About The Project
This project is part of **Udacity Android Nanodegree** program. This project provides the user a
main screen with movie posters in a grid layout. The user can click on any of the poster and
this will take him/her to the a different screen containing details about the movie. Further
user has the option to select between two sorting orders popular/top rated using the settings
menu option. Depending upo the sorting choice the main screen will get updated.

<img src=
"https://storage.googleapis.com/test-lab-4chkzc58ph1d6-wxj02k53w0uxz/web-build_2017-01-21T12%3A08%3A19.894Z_JJZF/zeroflte-22-en_US-portrait/artifacts/c0b446f0.png"alt="Drawing" style="width: 200px;"/>
<img src="https://storage.googleapis.com/test-lab-4chkzc58ph1d6-wxj02k53w0uxz/web-build_2017-01-21T12%3A08%3A19.894Z_JJZF/zeroflte-22-en_US-portrait/artifacts/014170b4.png" alt="Drawing" style="width: 200px;"/>
<img src="https://storage.googleapis.com/test-lab-4chkzc58ph1d6-wxj02k53w0uxz/web-build_2017-01-21T12%3A08%3A19.894Z_JJZF/zeroflte-22-en_US-portrait/artifacts/4dac13e3.png" alt="Drawing" style="width: 200px;"/>
<img src ="https://storage.googleapis.com/test-lab-4chkzc58ph1d6-wxj02k53w0uxz/web-build_2017-01-21T12%3A08%3A19.894Z_JJZF/osprey_umts-22-en_US-portrait/artifacts/094faef4.png" alt="Drawing" style="width: 200px;"/>


<img src ="https://storage.googleapis.com/test-lab-4chkzc58ph1d6-wxj02k53w0uxz/web-build_2017-01-21T12%3A08%3A19.894Z_JJZF/Nexus9-25-en_US-landscape/artifacts/9a76b072.jpg" alt="Drawing" style="width: 400px;"/>

#How to get API key
This project fetches movies information from [The Movie Database API](https://www.themoviedb.org/). This requires a key. In order to use the project make an account on The Movie Database. After that login to the account and goto the Account section API on the left side. Here you can find the API key under the option API.
Copy the key and add this key to the build.gradle file (for the app).

it.buildConfigField 'String', 'OPEN\_MOVIE\_API\_KEY', "\"add key here\""
