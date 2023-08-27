package smf.icdada

class Print {
    fun normalPrint(userId: Int, gem: Int? = null, snailCoin: Int? = null, chestnutPiece: Int? = null) {
        val output = "\u001B[32m账号：$userId\u001B[0m || \u001B[32m检测通过\u001B[0m || "
        val gemString = gem?.let { if (it >= 2000000) "\u001B[31m钻石：$it\u001B[0m - " else "\u001B[37m钻石：$it\u001B[0m - " } ?: "\u001B[37m钻石：null\u001B[0m - "
        val snailCoinString = snailCoin?.let { if (it >= 500000) "\u001B[31m蜗牛币：$it\u001B[0m - " else "\u001B[37m蜗牛币：$it\u001B[0m - " } ?: "\u001B[37m蜗牛币：null\u001B[0m - "
        val chestnutPieceString = chestnutPiece?.let { if (it >= 3000) "\u001B[31m荸荠碎片：$it\u001B[0m" else "\u001B[37m荸荠碎片：$it\u001B[0m" } ?: "\u001B[37m荸荠碎片：null\u001B[0m"
        println(output + gemString + snailCoinString + chestnutPieceString)
    }

    fun abnormalPrint(userId: Int, gem: Int? = null, snailCoin: Int? = null, chestnutPiece: Int? = null) {
        val output = "\u001B[33m账号：$userId\u001B[0m || \u001B[33m检测异常\u001B[0m || "
        val gemString = gem?.let { if (it >= 2000000) "\u001B[31m钻石：$it\u001B[0m - " else "\u001B[37m钻石：$it\u001B[0m - " } ?: "\u001B[37m钻石：null\u001B[0m - "
        val snailCoinString = snailCoin?.let { if (it >= 500000) "\u001B[31m蜗牛币：$it\u001B[0m - " else "\u001B[37m蜗牛币：$it\u001B[0m - " } ?: "\u001B[37m蜗牛币：null\u001B[0m - "
        val chestnutPieceString = chestnutPiece?.let { if (it >= 3000) "\u001B[31m荸荠碎片：$it\u001B[0m" else "\u001B[37m荸荠碎片：$it\u001B[0m" } ?: "\u001B[37m荸荠碎片：null\u001B[0m"
        println(output + gemString + snailCoinString + chestnutPieceString)
    }

}