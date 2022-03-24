package com.intellij.cce.processor

import com.intellij.cce.actions.*
import com.intellij.cce.core.CodeFragment
import com.intellij.cce.core.CodeToken
import com.intellij.cce.core.TypeProperty

class CallAutoImportProcessor(private val text: String,
                              private val strategy: CompletionStrategy,
                              textStart: Int) : GenerateActionsProcessor() {
  //private var previousTextStart = textStart

  override fun process(code: CodeFragment) {
    if (strategy.context == CompletionContext.ALL) {
      for (token in code.getChildren()) {
        if (token.properties.tokenType == TypeProperty.IMPORT_STATEMENT) processToken(token)
      }
      return
    }
  }

  private fun processToken(token: CodeToken) {
    if (!checkFilters(token)) return

    when (strategy.context) {
      CompletionContext.ALL -> prepareAllContext(token)
      CompletionContext.PREVIOUS -> preparePreviousContext(token)
    }

    addAction(CallAutoImport(token.text, token.properties))
    addAction(PrintText(TODO(), false)) //import text
    addAction(FinishSession())
  }

  private fun prepareAllContext(token: CodeToken) {
    addAction(DeleteRange(token.offset, token.offset + token.length)) //delete import for token
    addAction(MoveCaret(token.offset)) //move caret on token
  }

  private fun preparePreviousContext(token: CodeToken) {
    TODO()
  }

  private fun checkFilters(token: CodeToken) = strategy.filters.all { it.value.shouldEvaluate(token.properties) }
}