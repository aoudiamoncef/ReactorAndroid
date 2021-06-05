# ReactorAndroid Releases #

### Version 3.0.0 - February, 14 2020 ###

General availability of ReactorAndroid 3.0 for use with RxJava 3.0!

The Maven groupId has changed to `io.reactivex.rxjava3` and the package is now `com.aoudiamoncef.reactor.android`.

The APIs and behavior of ReactorAndroid 3.0.0 is otherwise exactly the same as ReactorAndroid 2.1.1 with one notable exception:

Schedulers created via `AndroidSchedulers.from` now deliver [async messages](https://developer.android.com/reference/android/os/Handler.html#createAsync(android.os.Looper)) by default.
This is also true for `AndroidSchedulers.mainThread()`.

For more information about RxJava 3.0 see [its release notes](https://github.com/ReactiveX/RxJava/releases/tag/v3.0.0).

---

Version 2.x can be found at https://github.com/ReactiveX/ReactorAndroid/blob/2.x/CHANGES.md

Version 1.x can be found at https://github.com/ReactiveX/ReactorAndroid/blob/1.x/CHANGES.md
