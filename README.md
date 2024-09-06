# N Keyboard

## [Download from the latest GitHub Release](https://github.com/quartz270/nth-kbd/releases/latest)

---

![Screenshot_20240907-021850](https://github.com/user-attachments/assets/6c76aace-ea82-4c84-a981-69f109a8372d)

![Screenshot_20240907-021921](https://github.com/user-attachments/assets/8e07a845-793c-4a21-a564-23b7f82e5c79)

- Added Translation Panel
- Added More ASCII Emojis
- Updated font
- Added themes - Dark, Light, Dynamic

---
# built over FUTO Keyboard

The goal is to make a good keyboard that doesn't spy on users. This repository is a fork of [LatinIME (the Android Open-Source Keyboard)](https://android.googlesource.com/platform/packages/inputmethods/LatinIME).

The code is licensed under the [FUTO Source First License 1.0](LICENSE.md).

Check out the [FUTO Keyboard Wiki](https://gitlab.futo.org/alex/keyboard-wiki/-/wikis/FUTO-Keyboard)


## Building

When cloning the repository, you must perform a recursive clone to fetch all dependencies:
```
git clone --recursive https://gitlab.futo.org/keyboard/latinime.git
```

You can also initialize this way if you forgot to specify the recursive clone:
```
git submodule update --init --recursive
```

You can then open the project in Android Studio and build it that way.
