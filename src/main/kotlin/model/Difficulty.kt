package model

import model.Difficulty.*

enum class Difficulty {
    Easy, Normal, Hard;
}

fun String?.toDifficulty() = when(this){
    Easy.toString() -> Easy
    Normal.toString() -> Normal
    Hard.toString() -> Hard
    else -> null
}
