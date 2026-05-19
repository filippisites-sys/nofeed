package com.nofeed.service

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo

object FeedDetector {

    fun isOnFeed(root: AccessibilityNodeInfo?): Boolean {
        root ?: return false
        return !isDMScreenVisible(root)
    }

    // Returns the bounding rect of the feed's main scrollable container.
    // Returns null if the feed RecyclerView isn't found yet (still loading).
    fun findFeedBounds(root: AccessibilityNodeInfo): Rect? {
        val candidates = mutableListOf<Rect>()
        collectFeedCandidates(root, candidates)
        return candidates
            .filter { it.width() > 0 && it.height() > 200 }
            .maxByOrNull { it.height() }
    }

    private fun collectFeedCandidates(node: AccessibilityNodeInfo, result: MutableList<Rect>) {
        if (node.isScrollable) {
            val rect = Rect()
            node.getBoundsInScreen(rect)
            // Feed is a wide vertical container. Stories are a short horizontal strip.
            // Filter: width must be large (full-width), height > 200dp worth of pixels.
            if (rect.width() > rect.height() * 0.4 && rect.height() > 200) {
                result.add(rect)
            }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { collectFeedCandidates(it, result) }
        }
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
