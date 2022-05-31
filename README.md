<p>
    <h1 align="center">
        <a href="https://play.google.com/store/apps/details?id=com.mac.ekchitthi">
            Ek Chitthi: Letter Posting App (JAVA) by Saurabh Kumar<br>PlayStore handle :- imSoloDeveloper
        </a>
    </h1>
</p>


<p align="center">
    <a href="https://play.google.com/store/apps/details?id=com.mac.ekchitthi">
        <img src="https://travis-ci.org/steverichey/google-play-badge-svg.svg?branch=master" alt="Build status">
    </a>
    <a href="https://github.com/dev-iamsaurabh/EkChitthi/blob/master/LICENSE">
        <img src="https://img.shields.io/badge/License-MIT-lightgrey.svg" alt="License: MIT">
    </a>
</p>

<p align="center">
  • <a href="#about">About</a>
  • <a href="#features">Features</a>
  • <a href="#technology-used">Technology Used</a>
  • <a href="#working">Working</a>
  • <a href="#available-on">Available on</a>
  • <a href="#license">License</a>


</p>



## About

Ek Chitthi App is a unique App and this is somthing new and my idea which i have created.
This idea came in my mind when i was watching move "The Lunchbox" made by bollywood industry.
Where two person met through letters and have great conversation between them through letters.
I thought in this fast messaging world, there are many people out who wanted to do such things like posting letter to someone and receving there reply after couple of days.
The idea behind this app is to have conversation between two people like old days. This app specially target couples for making their relationship more romantic and 
prevent from toxicity in relationship.
Idea is to post a Letter to someone according to distance. 
This is a social media App but not like Whatsapp , Facebook or any other , but this app is a kind of Letter posting app like old days.
You can send a letter to your friend with generating a stamp from your city to your friend city.
The Letter will will be deliver to your friend according to the distance more the distance it will take more days to reach to your friend.
Like your are living currently in Varanasi and your friend in Mumbai ,the approximate distance is about 1484 km and it will take 4 days to reach there .
You can schedule your letter too ..that on which date and on time you want to send this letter to your friend with the help of schedule functionality. 
Enjoy slow messaging and enhance your long relationship :)

## Features
-- You can send letter Anonymously.<br>
-- You can propose your crush without telling your name or number.
-- You can check the person is avaible on app or not by entering the phone number and if they are not available you invite them on Ek Chitthi.<br>
-- You can Generate a Stamp for your friend city. It will automatically calculate the days to reach the letter between two cities.<br> 
-- You can create a post card and paste the generated stamp and will show all the infomation about where this letter go and how many days it take to reach to its destianntion.<br>
-- You can schedule you letter when the letter will reach and what should its time to reach.<br>
-- You can track the letter that it is yet opened by your friend or not.<br>
-- You can block anyone.<br>
-- You will receive notification when the letter arrive.<br>
-- You can send letter to youself too.<br>

## Technology Used

-- Firebase Realtime Database:- Ek Chitthi uses Firebase Realtime Database to store its data and also User Authentication done by Firebase.<br>
-- Node.js:- I have used node.js script to send notification to user on scheduled time. This script is hosted on Firebase Cloud Function and runs everyminute
   and traverse in notification node and compare time stamp from current time stamp . When the current time stamp is greater than shceduled time stamp, then the script
   sends the notification to the user with the help Firebase Cloud messaging(FCM).<br>
-- Firebase Cloud Function :- To host node.js script.<br>
-- Firebase Cloud Messaging (FCM) :- To send notification to user on letter received.<br>


## Working
-- User create new Post Card with Scheduled TimeStamp.<br>
-- Sends to the Database.<br>
-- Node.js script checks the TimeStamp every minute.<br>
-- When Scheduled TimeStamp is less than current time stamp ,Scripts send the notification to user.<br>






## Available on

<p align="center">
<a href="https://play.google.com/store/apps/details?id=com.mac.ekchitthic">
<img src="https://cdn.jsdelivr.net/gh/dev-iamsaurabh/BMICalculator/play.svg" width="50%">
</a>
</p>


## License

Unless covered under some other license, all content in this repository is shared under an MIT license. See [license.md](./license.md) for details.

Google Play and the Google Play logo are trademarks of Google Inc. Be sure to read the [Branding Guidelines](https://developer.android.com/distribute/tools/promote/brand.html) and contact Google via the [Android and Google Play Brand Permissions Inquiry form](https://support.google.com/googleplay/contact/brand_developer) if you have any questions. SVGs in this repository were generated from files provided by Google [here](https://play.google.com/intl/en_us/badges/) and they have all the copyrights and trademarks and whatevs.

  
