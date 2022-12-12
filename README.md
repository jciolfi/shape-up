# CS 5520, Fall 2022: NUMAD22Fa_Team27


## Team Commits

**PLEASE NOTE** - if you look at the Contributors graph, you'll notice some users don't show up and a lot of commits are unaccounted for. We've talked with a TA about this. It's because not all of us configured our Git instance in a way that's compatible with insights. You can check the contributions yourself from either the Pulse page, or by command line:


```bash
# Number of commits per user
Ben_M@DESKTOP-GHIMRMC MINGW64 ~/AndroidStudioProjects/NUMAD22Fa_Team27 (master)
$ git shortlog -sn
    89  bmontgom
    71  John Ciolfi
    50  farzadjahandar
    36  Ben Montgomery
    35  fabigazi
     7  fjahandar
     2  Farzad

# Lines of code authored per user
Ben_M@DESKTOP-GHIMRMC MINGW64 ~/AndroidStudioProjects/NUMAD22Fa_Team27 (master)
$ git ls-files | while read f; do git blame -w -M -C -C --line-porcelain "$f" | grep -I '^author '; done | sort -f | uniq -ic | sort -n --reverse
   6364 author bmontgom
   4265 author John Ciolfi
   1759 author farzadjahandar
   1318 author fabigazi
    223 author fjahandar
     51 author Ben Montgomery


```

We believe that our team has contributed evenly of the course of this project. XML files are massive and increase the line count; commit size strategy varies somewhat from person to person; and sometimes there's work that isn't captured, like spending long hours experimenting with code at the need of the team that turns out to be a dead end.

## Proposal Recap

### Target Users
Adults with limited fitness experience looking to stay active with friends.

### What problem/task(s)/need does the application help the users address?
The app tries to motivate users to work out, even if they haven’t felt up to it previously. It allows
users to track their workout program and progress in a fun and engaging way, with support from
friends.

#### What three current apps on the Play Store (or other app stores, such as iTunes) would be your closest competitors?
* Apple Fitness
* Beginners Gym Workout
* 7 Minute Workout


### What about your app design will keep people engaged using it for a long time, even once the novelty wears off?
The users are encouraged to continue to use the app due to the social aspect as well as the gamification of workouts. Users can make new friends who mutually encourage each other to
continue to stay fit. The potential real world benefits, such as users achieving health goals, could also be another factor in keeping users engaged for a long time
