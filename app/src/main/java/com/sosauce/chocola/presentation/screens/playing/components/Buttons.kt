@file:OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package com.sosauce.chocola.presentation.screens.playing.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sosauce.chocola.R
import com.sosauce.chocola.data.states.MusicState
import com.sosauce.chocola.domain.actions.PlayerActions
import com.sosauce.chocola.presentation.shared_components.animations.AnimatedPlayPauseIcon
import com.sosauce.chocola.utils.rememberInteractionSource

@Composable
fun PlayPauseButton(
    modifier: Modifier = Modifier,
    buttonModifier: Modifier = Modifier,
    isPlaying: Boolean,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val interactionSource = rememberInteractionSource()
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f
    )


    IconButton(
        onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
        modifier = buttonModifier,
        interactionSource = interactionSource
    ) {
        AnimatedPlayPauseIcon(
            isPlaying = isPlaying,
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
fun ActionButtonsRow(
    musicState: MusicState,
    onHandlePlayerActions: (PlayerActions) -> Unit
) {

    val prevInteractionSource = rememberInteractionSource()
    val playPauseInteractionSource = rememberInteractionSource()
    val nextInteractionSource = rememberInteractionSource()

    val isPrevPressed by prevInteractionSource.collectIsPressedAsState()
    val isPlayPausePressed by playPauseInteractionSource.collectIsPressedAsState()
    val isNextPressed by nextInteractionSource.collectIsPressedAsState()

    // Metrolist-style mutual weight animation: the pressed button grows,
    // its neighbors shrink, using a bouncy spring for that "expressive" feel.
    val playPauseWeight by animateFloatAsState(
        targetValue = when {
            isPlayPausePressed -> 1.9f
            isPrevPressed || isNextPressed -> 1.1f
            else -> 1.3f
        },
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "playPauseWeight"
    )

    val prevWeight by animateFloatAsState(
        targetValue = when {
            isPrevPressed -> 0.65f
            isPlayPausePressed -> 0.35f
            else -> 0.45f
        },
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "prevWeight"
    )

    val nextWeight by animateFloatAsState(
        targetValue = when {
            isNextPressed -> 0.65f
            isPlayPausePressed -> 0.35f
            else -> 0.45f
        },
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "nextWeight"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilledIconButton(
            onClick = { onHandlePlayerActions(PlayerActions.SeekToPreviousMusic) },
            shape = RoundedCornerShape(50),
            interactionSource = prevInteractionSource,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = contentColorFor(MaterialTheme.colorScheme.tertiary)
            ),
            modifier = Modifier
                .height(68.dp)
                .weight(prevWeight)
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_previous),
                contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
            )
        }

        FilledIconButton(
            onClick = { onHandlePlayerActions(PlayerActions.PlayOrPause) },
            shape = RoundedCornerShape(50),
            interactionSource = playPauseInteractionSource,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = contentColorFor(MaterialTheme.colorScheme.primary)
            ),
            modifier = Modifier
                .height(68.dp)
                .weight(playPauseWeight)
        ) {
            AnimatedPlayPauseIcon(
                isPlaying = musicState.isPlaying
            )
        }

        FilledIconButton(
            onClick = { onHandlePlayerActions(PlayerActions.SeekToNextMusic) },
            shape = RoundedCornerShape(50),
            interactionSource = nextInteractionSource,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = contentColorFor(MaterialTheme.colorScheme.tertiary)
            ),
            modifier = Modifier
                .height(68.dp)
                .weight(nextWeight)
        ) {
            Icon(
                painter = painterResource(R.drawable.skip_next),
                contentDescription = stringResource(androidx.media3.session.R.string.media3_controls_seek_forward_description)
            )
        }
    }
}
