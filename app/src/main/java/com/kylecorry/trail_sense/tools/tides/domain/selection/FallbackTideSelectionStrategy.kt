package com.kylecorry.trail_sense.tools.tides.domain.selection

import com.kylecorry.trail_sense.tools.tides.domain.TideTable

class FallbackTideSelectionStrategy(private vararg val strategies: ITideSelectionStrategy) :
    ITideSelectionStrategy {
    override suspend fun getTide(tides: List<TideTable>): TideTable? {
        for (strategy in strategies) {
            val tide = strategy.getTide(tides)
            if (tide != null) {
                return tide
            }
        }
        return null
    }
}