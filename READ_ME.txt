LocationUpdateApp
by Jack Hale

How to run the project:
This project that can be opened in android studio. You can then use a VM or a paired device through
the device manager to run the app.

Implementation Decisions and Trade-Offs:
I interpreted the instruction "Every time the user moves at least 10m" to mean every time the user
crosses a boundary that lies a multiple of 10 metres from their initial position. I also included a
button so that the user could reset their initial position to their current position.
This means that you can move MORE than ten metres and not receive an update, as long as its in a
circular shape, but I thought it more made sense anyway to measure the distances from a fixed point.

Time spent working on the App:
I spent about a day completing the project. This is obviously longer than the "few hours" that were
recommended, but I hadn't done any android coding in a while and I needed the extra time. A lot of
it was spent trying to figure out how to use the google location APIs. As you can probably see,
there are still some parts (like the permission checking) that aren't implemented well and I am
aware of that, but I didn't want to spend any more time on it after going way over time already. I
also couldn't get unit tests working properly, so I didn't include them either. I hope it's not a
waste of your time for me to hand in an incomplete project, but I did put effort in and I would
love to get your feedback on how to improve.

Areas of focus:
I focused on ensuring the app had the functionality specified in the instructions. Next time, I
would spend more time learning how to implement the testing requirements.

Copied code, references, and third party libraries:
I used the google location APIs, and used developer.android.com as a guide for implementation, but
I didn't copy any code (excluding a few guide snippets on developer.android.com) or use other
third party libraries.

Other notes:
Thanks for taking the time to check out my work. Even if I'm not successful, it was a fun little
project!

