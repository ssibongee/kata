class KOuter {

    private var x = 1

    class KSInner {

    }

    inner class KInner {
        fun foo() {
            this@KOuter.x = 3
        }
    }
}