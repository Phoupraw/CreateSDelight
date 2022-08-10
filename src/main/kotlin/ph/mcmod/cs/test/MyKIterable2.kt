package ph.mcmod.cs.test

import java.util.*

class MyKIterable2 : Iterable<Int> {
    override fun iterator(): Iterator<Int> {
        return object :Iterator<Int>{
            override fun hasNext(): Boolean {
                TODO("Not yet implemented")
            }
    
            override fun next(): Int {
                TODO("Not yet implemented")
            }
        }
    }
}