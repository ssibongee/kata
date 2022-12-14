sealed class Expr {
}

class Num(val value: Int) : Expr()

class Sum(val left: Expr, val right: Expr) : Expr()

fun eval(e: Expr): Int =
    when (e) {
        is Num -> e.value
        is Sum -> eval(e.left) + eval(e.right)
        is Sub -> eval(e.left) - eval(e.right)
    }