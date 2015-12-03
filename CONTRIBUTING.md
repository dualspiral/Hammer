####Contributing to Hammer

I'm glad you want to help! Hammer is meant to be the next generation in ban management, and to do that, it needs your help! However, there are a few guidelines that I ask you follow before you submit your pull request:

* Most common code **must** go in HammerCore. The Sponge/Bukkit implementations are nothing more than shims to the core.
* Unless there is a VERY good reason, you MUST support all target implementations. This is currently Bukkit/Cauldron 1.7.10, Spigot 1.8.8+ and the latest version of the Sponge API (which will become version 3).
    * If you aren't sure how to do something in one of the implementations, that's OK! Label your PR as such, and I'm sure we can help flesh it out!
    * Note that I may retire target implementations at any time. It is likely that 1.7.10 will be retired when 1.9 is stable enough.
* Absolutely DO add new abstractions to the core and corresponding implementations if you feel you need it. The abstractions are there to help give a consistent experience across all platforms.
* TEST TEST TEST. Check what you've written and make sure it works before you PR!
* DO NOT PR for ONLY minor gramatical issues or spelling (but feel free to inclue a commit as part of a larger PR)
* Include the licence block *word-for-word* at the top of each file in any new files you create.
* Finally, you agree that any code you contribute will be licenced under the MIT licence, and may be used in accordance with that licence.
 
We'll get along great if you follow these rules. Thanks for contributing!