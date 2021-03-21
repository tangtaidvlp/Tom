package com.teamttdvlp.memolang.data.model.other.new_vocabulary

import java.io.Serializable
import java.lang.Exception

class TypicalRawVocabulary (key : String, var fullContent: ArrayList<String> = ArrayList()) : RawVocabulary(key), Serializable {

    fun deepClone(): TypicalRawVocabulary {
        val clonedFullContent = ArrayList<String>()
        clonedFullContent.addAll(fullContent)
        val clonedObject = TypicalRawVocabulary(key, clonedFullContent)

        if (clonedObject != this) throw ChangeObjectStructureWithoutModifyingCloneMethod()

        return clonedObject
    }

    override fun equals(other: Any?): Boolean {
        if (other is TypicalRawVocabulary) {
            return (other.key == this.key) && (other.fullContent.containsAll(this.fullContent) && (other.fullContent.size == this.fullContent.size))
        }
        return super.equals(other)
    }

}

class NavigableRawVocabulary (key : String, targetVocaKey : String) : RawVocabulary(key) {
    var targetVocaKey : String = targetVocaKey
    private set

    public override fun clone(): NavigableRawVocabulary {
        return super.clone() as NavigableRawVocabulary
    }
}

abstract class RawVocabulary (key : String) : Object(), Serializable {
    var key : String = key
    private set
}

class ChangeObjectStructureWithoutModifyingCloneMethod () : Exception()