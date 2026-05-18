package com.esmanureral.neurostage.ui.patient.hub

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.esmanureral.neurostage.R
import com.esmanureral.neurostage.ui.theme.PatientColors
import com.esmanureral.neurostage.ui.theme.PatientDimens

private val CaveatFamily = FontFamily(Font(R.font.caveat_regular))

private const val NoteWidthFraction = 0.78f

@Composable
fun ProgramHubScrapbookQuoteCard(
    quote: HubMotivationQuote,
    modifier: Modifier = Modifier,
) {
    if (quote.text.isBlank()) return

    val titleColor = PatientColors.primary
    val quoteStyle = quoteTextStyle(quote.text)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.patient_hub_quote_daily_title),
            fontSize = PatientDimens.homeMildTitleSize,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 6.dp),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(modifier = Modifier.fillMaxWidth(NoteWidthFraction)) {
                Image(
                    painter = painterResource(R.drawable.note),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(),
                )

                BoxWithConstraints(modifier = Modifier.matchParentSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = maxWidth * 0.14f,
                                end = maxWidth * 0.12f,
                                top = maxHeight * 0.20f,
                                bottom = maxHeight * 0.18f,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = quote.text,
                            style = quoteStyle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun quoteTextStyle(text: String): TextStyle {
    val fontSize = quoteFontSize(text.length)
    val lineHeight = (fontSize.value * 1.2f).sp
    return TextStyle(
        fontFamily = CaveatFamily,
        fontSize = fontSize,
        fontWeight = FontWeight.Normal,
        color = colorResource(R.color.patient_hub_scrapbook_ink),
        lineHeight = lineHeight,
    )
}

private fun quoteFontSize(charCount: Int): TextUnit = when {
    charCount <= 42 -> 30.sp
    charCount <= 58 -> 27.sp
    charCount <= 76 -> 24.sp
    charCount <= 92 -> 22.sp
    else -> 19.sp
}
