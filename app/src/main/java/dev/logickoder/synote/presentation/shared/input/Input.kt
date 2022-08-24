package dev.logickoder.synote.presentation.shared.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.logickoder.synote.R
import dev.logickoder.synote.core.theme.AppTheme
import dev.logickoder.synote.core.theme.padding
import dev.logickoder.synote.core.theme.secondaryPadding
import dev.logickoder.synote.presentation.shared.ErrorText

@Composable
private fun InputTitle(
    text: String,
    modifier: Modifier = Modifier,
    error: Boolean = false,
    required: Boolean = false,
    color: Color,
) {
    val contentColor = if (error) AppTheme.colors.error else color
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(secondaryPadding() / 4),
        content = {
            Text(
                text = text,
                style = AppTheme.typography.body2.copy(
                    fontWeight = FontWeight.Medium,
                    color = contentColor
                )
            )
            if (required) Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(contentColor, CircleShape)
            )
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InputField(
    state: InputState,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) = with(state) {
    val color = state.color ?: AppTheme.colors.onSurface
    Column(
        content = {
            BasicTextField(
                modifier = modifier
                    .background(
                        color = (if (error == null) color else AppTheme.colors.error).copy(alpha = 0.1f),
                        shape = AppTheme.shapes.medium,
                    ),
                value = value,
                onValueChange = onValueChanged,
                textStyle = AppTheme.typography.body1.copy(
                    color = color.content(),
                ),
                singleLine = singleLine,
                enabled = enabled,
                readOnly = readOnly,
                visualTransformation = visualTransformation,
                interactionSource = interactionSource,
                keyboardOptions = keyboardOptions,
                decorationBox = { innerTextField ->
                    TextFieldDefaults.TextFieldDecorationBox(
                        value = value,
                        enabled = enabled,
                        singleLine = singleLine,
                        visualTransformation = visualTransformation,
                        isError = error != null,
                        innerTextField = {
                            val padding = 2.dp
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(padding),
                                content = {
                                    val textField = @Composable {
                                        Box(
                                            modifier = Modifier.weight(1f),
                                            content = {
                                                innerTextField()
                                            }
                                        )
                                    }
                                    val iconComposable = @Composable {
                                        if (icon != null) Icon(
                                            modifier = Modifier.size(20.dp).run {
                                                icon.onClick?.let {
                                                    clickable { it() }
                                                } ?: this
                                            },
                                            imageVector = icon.icon,
                                            contentDescription = null,
                                            tint = color.content(),
                                        )
                                    }
                                    if (icon?.alignEnd == true) {
                                        textField()
                                        iconComposable()
                                    } else {
                                        iconComposable()
                                        textField()
                                    }
                                }
                            )
                        },
                        contentPadding = PaddingValues(padding() / 4),
                        interactionSource = interactionSource,
                    )
                }
            )
            if (error != null) ErrorText(error = error)
        }
    )
}

@Composable
fun Input(
    title: String,
    state: InputState,
    modifier: Modifier = Modifier,
    content: @Composable (MutableInteractionSource, InputState) -> Unit = { interactionSource, inputState ->
        InputField(
            modifier = Modifier.fillMaxWidth(),
            state = inputState,
            interactionSource = interactionSource,
        )
    },
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(secondaryPadding() / 4),
        content = {
            val interactionSource = remember { MutableInteractionSource() }
            val focused by interactionSource.collectIsFocusedAsState()
            InputTitle(
                text = title,
                error = state.error != null,
                required = state.required,
                color = if (!focused) {
                    (state.color ?: AppTheme.colors.onSurface).content()
                } else AppTheme.colors.primary.content(),
            )
            content(interactionSource, state)
        }
    )
}

@Composable
fun PasswordInput(
    state: InputState,
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.password),
) {
    var passwordVisible by remember {
        mutableStateOf(false)
    }

    Input(
        title = title,
        modifier = modifier,
        state = state.copy(
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else PasswordVisualTransformation(),
            icon = IconData(
                icon = if (passwordVisible) {
                    Icons.Outlined.Visibility
                } else Icons.Outlined.VisibilityOff,
                onClick = {
                    passwordVisible = !passwordVisible
                }
            ),
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun InputPreview() = Input(
    title = "InputPreview",
    state = InputState(value = "InputPreview")
)

@Preview(showBackground = true)
@Composable
private fun PasswordInputPreview() = PasswordInput(
    title = "PasswordInputPreview",
    state = InputState(value = "PasswordInputPreview")
)