package com.nofeed.service

import android.view.accessibility.AccessibilityNodeInfo

object FeedDetector {

    // Block everything in Instagram EXCEPT the DM screens.
    // DMs are allowed; everything else (feed, explore, reels, profile) is blocked.
    fun isOnFeed(root: AccessibilityNodeInfo?): Boolean {
        root ?: return false
        return !isDMScreenVisible(root)
    }

    private fun isDMScreenVisible(root: AccessibilityNodeInfo): Boolean {
        val dmTexts = listOf(
            "Direct", "Messages", "Mensagens", "Chats",
            "New message", "Nova mensagem", "Inbox", "Caixa de entrada"
        )
        val candidates = mutableListOf<AccessibilityNodeInfo>()
        dmTexts.forEach { text -> collectByText(root, text, candidates) }
        return candidates.isNotEmpty()
    }

    private fun collectByText(
        node: AccessibilityNodeInfo,
        text: String,
        result: MutableList<AccessibilityNodeInfo>
    ) {
        val nodeText = node.text?.toString() ?: ""
        val nodeDesc = node.contentDescription?.toString() ?: ""
        if (nodeText.contains(text, ignoreCase = true) ||
            nodeDesc.contains(text, ignoreCase = true)
        ) {
            result.add(node)
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { collectByText(it, text, result) }
        }
    }
}
