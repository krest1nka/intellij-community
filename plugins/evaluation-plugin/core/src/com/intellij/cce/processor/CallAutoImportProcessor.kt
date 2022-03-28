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
      for (import in code.getChildren()) {
        if (import.properties.tokenType == TypeProperty.IMPORT_STATEMENT) {
          var expectedQualifiedName: String = import.text.substring(7)
          System.out.println(expectedQualifiedName)
          for (token in code.getChildren()) {
            if (token.properties.additionalProperty("qualified name") == expectedQualifiedName) process(token, import)
          }
        }
      }
      return
    }
  }

  private fun process(token: CodeToken, import: CodeToken) {
    //if (!checkFilters(token)) return

    when (strategy.context) {
      CompletionContext.ALL -> prepareAllContext(token, import)
      //CompletionContext.PREVIOUS -> preparePreviousContext(token)
    }

    addAction(CallAutoImport(import.text, import.properties, token.properties)) //TODO
    addAction(PrintText(import.text, false))
    addAction(FinishSession())
  }



  private fun prepareAllContext(token: CodeToken, import: CodeToken) {
    addAction(DeleteRange(import.offset, import.offset + import.length)) //delete token
    addAction(MoveCaret(token.offset)) //move caret on token
  }

  private fun preparePreviousContext(token: CodeToken) {
    TODO()
  }

  private fun checkFilters(token: CodeToken) = strategy.filters.all { it.value.shouldEvaluate(token.properties) }
}