package com.nofeed.service

import android.view.accessibility.AccessibilityNodeInfo

object FeedDetector {

    fun isOnFeed(root: AccessibilityNodeInfo?): Boolean {
        root ?: return false
        return isHomeTabSelected(root) && !isDMScreenVisible(root)
    }

    private fun isHomeTabSelected(root: AccessibilityNodeInfo): Boolean {
        val candidates = mutableListOf<AccessibilityNodeInfo>()
        collectByText(root, "Home", candidates)
        collectByText(root, "Início", candidates)      // pt-BR
        collectByText(root, "Inicio", candidates)      // es
        return candidates.any { node ->
            node.isSelected || node.isChecked ||
                node.parent?.isSelected == true ||
                node.parent?.isChecked == true
        }
    }

    private fun isDMScreenVisible(root: AccessibilityNodeInfo): Boolean {
        val candidates = mutableListOf<AccessibilityNodeInfo>()
        collectByText(root, "Direct", candidates)
        collectByText(root, "Messages", candidates)
        collectByText(root, "Mensagens", candidates)
        collectByText(root, "Chats", candidates)
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
