// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.jetbrains.kotlin.idea.parameterInfo

import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.idea.codeInsight.hints.*
import org.jetbrains.kotlin.idea.core.util.isOneLiner
import org.jetbrains.kotlin.idea.util.safeAnalyzeNonSourceRootCode
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext.USED_AS_RESULT_OF_LAMBDA
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

@Suppress("UnstableApiUsage")
fun provideLambdaReturnValueHints(expression: KtExpression): InlayInfoDetails? {
    if (expression is KtWhenExpression || expression is KtBlockExpression) {
        return null
    }

    if (expression is KtIfExpression && !expression.isOneLiner()) {
        return null
    }

    if (expression.getParentOfType<KtIfExpression>(true)?.isOneLiner() == true) {
        return null
    }

    if (!KtPsiUtil.isStatement(expression)) {
        if (!allowLabelOnExpressionPart(expression)) {
            return null
        }
    } else if (forceLabelOnExpressionPart(expression)) {
        return null
    }

    val functionLiteral = expression.getParentOfType<KtFunctionLiteral>(true)
    val body = functionLiteral?.bodyExpression ?: return null
    if (body.statements.size == 1 && body.statements[0] == expression) {
        return null
    }

    val bindingContext = expression.safeAnalyzeNonSourceRootCode(BodyResolveMode.PARTIAL_WITH_CFA)
    if (bindingContext[USED_AS_RESULT_OF_LAMBDA, expression] == true) {
        val lambdaExpression = expression.getStrictParentOfType<KtLambdaExpression>() ?: return null
        val lambdaName = lambdaExpression.getNameOfFunctionThatTakesLambda() ?: "lambda"
        val inlayInfo = InlayInfo("", expression.endOffset)
        return InlayInfoDetails(inlayInfo, listOf(TextInlayInfoDetail("^"), PsiInlayInfoDetail(lambdaName, lambdaExpression)))
    }
    return null
}

private fun KtLambdaExpression.getNameOfFunctionThatTakesLambda(): String? {
    val lambda = this
    val callExpression = this.getStrictParentOfType<KtCallExpression>() ?: return null
    if (callExpression.lambdaArguments.any { it.getLambdaExpression() == lambda }) {
        val parent = lambda.parent
        if (parent is KtLabeledExpression) {
            return parent.getLabelName()
        }
        return (callExpression.calleeExpression as? KtNameReferenceExpression)?.getReferencedName()
    }
    return null
}

private fun allowLabelOnExpressionPart(expression: KtExpression): Boolean {
    val parent = expression.parent as? KtExpression ?: return false
    return expression == expressionStatementPart(parent)
}

private fun forceLabelOnExpressionPart(expression: KtExpression): Boolean {
    return expressionStatementPart(expression) != null
}

private fun expressionStatementPart(expression: KtExpression): KtExpression? {
    val splitPart: KtExpression = when (expression) {
        is KtAnnotatedExpression -> expression.baseExpression
        is KtLabeledExpression -> expression.baseExpression
        else -> null
    } ?: return null

    if (!isNewLineBeforeExpression(splitPart)) {
        return null
    }

    return splitPart
}

private fun isNewLineBeforeExpression(expression: KtExpression): Boolean {
    val whiteSpace = expression.node.treePrev?.psi as? PsiWhiteSpace ?: return false
    return whiteSpace.text.contains("\n")
}
