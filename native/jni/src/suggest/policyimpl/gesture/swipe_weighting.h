#pragma once

#include "suggest/core/dicnode/dic_node.h"
#include "suggest/core/session/dic_traverse_session.h"
#include "suggest/core/layout/proximity_info.h"
#include "suggest/core/policy/weighting.h"
#include "suggest/policyimpl/typing/scoring_params.h"

namespace util {
    static AK_FORCE_INLINE int getDistanceBetweenPoints(const latinime::DicTraverseSession *const traverseSession, int codePoint, int index) {
        auto proximityInfoState = traverseSession->getProximityInfoState(0);
        auto proximityInfo = traverseSession->getProximityInfo();
        int px = proximityInfoState->getInputX(index);
        int py = proximityInfoState->getInputY(index);

        int keyIdx = proximityInfo->getKeyIndexOf(latinime::CharUtils::toBaseLowerCase(codePoint));
        int kx = proximityInfo->getSweetSpotCenterXAt(keyIdx);
        int ky = proximityInfo->getSweetSpotCenterYAt(keyIdx);

        return sqrtf(latinime::GeometryUtils::getDistanceSq(px, py, kx, ky));
    }

    static AK_FORCE_INLINE float findMinimumPointDistance(int px, int py, int l0x, int l0y, int l1x, int l1y) {
        int ax = l0x;
        int ay = l0y;
        int bx = l1x - l0x;
        int by = l1y - l0y;

        if(bx == 0 && by == 0) {
            int dx = px - ax;
            int dy = py - ay;
            return (dx * dx + dy * dy);
        }

        int p_dot_b = px * bx + py * by;
        int a_dot_b = ax * bx + ay * by;
        int b_len_sq = bx * bx + by * by;
        float t = (float)(p_dot_b - a_dot_b) / (float)b_len_sq;
        if(t < 0.0f) t = 0.0f;
        if(t > 1.0f) t = 1.0f;

        float cx = (px - (ax + t * bx));
        float cy = (py - (ay + t * by));

        return sqrtf(cx * cx + cy * cy);
    }

    static AK_FORCE_INLINE float getDistanceLine(const latinime::DicTraverseSession *const traverseSession, int codePoint, int index0, int index1) {
        auto proximityInfoState = traverseSession->getProximityInfoState(0);
        auto proximityInfo = traverseSession->getProximityInfo();
        int l0x = proximityInfoState->getInputX(index0);
        int l0y = proximityInfoState->getInputY(index0);
        int l1x = proximityInfoState->getInputX(index1);
        int l1y = proximityInfoState->getInputY(index1);

        int keyIdx = proximityInfo->getKeyIndexOf(latinime::CharUtils::toBaseLowerCase(codePoint));
        int px = proximityInfo->getSweetSpotCenterXAt(keyIdx);
        int py = proximityInfo->getSweetSpotCenterYAt(keyIdx);

        return findMinimumPointDistance(px, py, l0x, l0y, l1x, l1y);
    }

    static AK_FORCE_INLINE float getDistanceCodePointLine(const latinime::DicTraverseSession *const traverseSession, int codePoint0, int codePoint1, int index) {
        auto proximityInfoState = traverseSession->getProximityInfoState(0);
        auto proximityInfo = traverseSession->getProximityInfo();
        int px = proximityInfoState->getInputX(index);
        int py = proximityInfoState->getInputY(index);

        int keyIdx0 = proximityInfo->getKeyIndexOf(latinime::CharUtils::toBaseLowerCase(codePoint0));
        int keyIdx1 = proximityInfo->getKeyIndexOf(latinime::CharUtils::toBaseLowerCase(codePoint1));
        int l0x = proximityInfo->getSweetSpotCenterXAt(keyIdx0);
        int l0y = proximityInfo->getSweetSpotCenterYAt(keyIdx0);
        int l1x = proximityInfo->getSweetSpotCenterXAt(keyIdx1);
        int l1y = proximityInfo->getSweetSpotCenterYAt(keyIdx1);

        return findMinimumPointDistance(px, py, l0x, l0y, l1x, l1y);
    }

    static AK_FORCE_INLINE float pow2(float f){
        return f * f;
    }

    static AK_FORCE_INLINE float calcLineDeviationPunishment(
            const latinime::DicTraverseSession *const traverseSession,
            int codePoint0, int codePoint1,
            int lowerLimit, int upperLimit,
            float threshold
    ) {
        float totalDistance = 0.0;

        const int ki_0 = traverseSession->getProximityInfo()->getKeyIndexOf(latinime::CharUtils::toBaseLowerCase(codePoint0));
        const int ki_1 = traverseSession->getProximityInfo()->getKeyIndexOf(latinime::CharUtils::toBaseLowerCase(codePoint1));

        const float l0x = traverseSession->getProximityInfo()->getSweetSpotCenterXAt(ki_0);
        const float l0y = traverseSession->getProximityInfo()->getSweetSpotCenterYAt(ki_0);

        const float l1x = traverseSession->getProximityInfo()->getSweetSpotCenterXAt(ki_1);
        const float l1y = traverseSession->getProximityInfo()->getSweetSpotCenterYAt(ki_1);

        for(int j = lowerLimit; j < upperLimit; j++) {
            const float distance = getDistanceCodePointLine(traverseSession, codePoint0, codePoint1, j);
            totalDistance += distance;

            if(distance > threshold) {
                //AKLOGI("Attention please: at %d (%d->%d) [%c->%c], distance %.2f exceeds threshold %.2f", j, lowerLimit, upperLimit, (char)codePoint0, (char)codePoint1, distance, threshold);
                return MAX_VALUE_FOR_WEIGHTING;
            }


            if(j > 1) {
                const float px  = traverseSession->getProximityInfoState(0)->getInputX(j);
                const float py  = traverseSession->getProximityInfoState(0)->getInputY(j);

                const float pxp = traverseSession->getProximityInfoState(0)->getInputX(j - 1);
                const float pyp = traverseSession->getProximityInfoState(0)->getInputY(j - 1);

                float swipedx = px - pxp;
                float swipedy = py - pyp;
                const float swipelen = sqrtf(swipedx * swipedx + swipedy * swipedy);
                swipedx /= swipelen;
                swipedy /= swipelen;

                float linedx = l1x - l0x;
                float linedy = l1y - l0y;
                const float linelen = sqrtf(linedx * linedx + linedy * linedy);
                linedx /= linelen;
                linedy /= linelen;

                const float dotDirection = swipedx * linedx + swipedy * linedy;

                if (dotDirection < 0.0) {
                    totalDistance += swipelen * -dotDirection;
                }
            }

        }

        return totalDistance;
    }

    static AK_FORCE_INLINE float getThresholdBase(const latinime::DicTraverseSession *const traverseSession) {
        return traverseSession->getProximityInfo()->getMostCommonKeyWidth() / 48.0f;
    }
}

namespace latinime {
class SwipeWeighting : public Weighting {
public:
    static const SwipeWeighting *getInstance() { return &sInstance; }

    AK_FORCE_INLINE float getTerminalSpatialCost(const DicTraverseSession *const traverseSession,
                                 const DicNode *const parentDicNode,
                                 const DicNode *const dicNode) const override {
        const int codePoint = dicNode->getNodeCodePoint();

        const float distanceThreshold = util::getThresholdBase(traverseSession);

        const float distance = util::getDistanceBetweenPoints(traverseSession, codePoint,
                traverseSession->getInputSize() - 1);

        if(distance > (distanceThreshold * 128.0f)) {
            //AKLOGI("Terminal spatial for %c:%c fails due to exceeding distance", (parentDicNode != nullptr) ? (char)(parentDicNode->getNodeCodePoint()) : '?', (char)codePoint);
            //dicNode->dump("TERMINAL");

            return MAX_VALUE_FOR_WEIGHTING;
        }

        float totalDistance = distance * distance;

        if(parentDicNode != nullptr) {
            const int codePoint0 = parentDicNode->getNodeCodePoint();
            const int codePoint1 = codePoint;

            const int lowerLimit = dicNode->getInputIndex(0);
            const int upperLimit = traverseSession->getInputSize();

            const float threshold = (distanceThreshold * 86.0f);

            const float extraDistance = 8.0f * util::calcLineDeviationPunishment(
                    traverseSession, codePoint0, codePoint1, lowerLimit, upperLimit, threshold);

            totalDistance += extraDistance;

            //AKLOGI("Terminal spatial for %c:%c - %d:%d : extra %.2f %.2f", (char)codePoint0, (char)codePoint1, lowerLimit, upperLimit, distance, extraDistance);
            //dicNode->dump("TERMINAL");

            return totalDistance;
        } else {
            AKLOGE("Nullptr parent unexpected! for terminal");
            return MAX_VALUE_FOR_WEIGHTING;
        }
    }

    AK_FORCE_INLINE float getOmissionCost(const DicNode *const parentDicNode, const DicNode *const dicNode) const override {
        const bool isZeroCostOmission = parentDicNode->isZeroCostOmission();
        const bool isIntentionalOmission = parentDicNode->canBeIntentionalOmission();
        const bool sameCodePoint = dicNode->isSameNodeCodePoint(parentDicNode);
        // If the traversal omitted the first letter then the dicNode should now be on the second.
        const bool isFirstLetterOmission = dicNode->getNodeCodePointCount() == 2;
        float cost = MAX_VALUE_FOR_WEIGHTING;

        if(isZeroCostOmission || isIntentionalOmission || isFirstLetterOmission || sameCodePoint) {
            cost = 0.0f;
        }

        return cost;
    }

    AK_FORCE_INLINE float getMatchedCost(const DicTraverseSession *const traverseSession, const DicNode *const parentDicNode,
                                         const DicNode *const dicNode, DicNode_InputStateG *inputStateG) const override {
        const int codePoint = dicNode->getNodeCodePoint();

        const float distanceThreshold = util::getThresholdBase(traverseSession);

        if(dicNode->isFirstLetter()) { // Add the first point (from when swiping starts)
            const float distance = util::getDistanceBetweenPoints(traverseSession, codePoint, 0);

            if (distance < (40.0f * distanceThreshold)) {
                inputStateG->mNeedsToUpdateInputStateG = true;
                inputStateG->mInputIndex = 1;
                inputStateG->mRawLength = distance;

                return distance;
            } else {
                return MAX_VALUE_FOR_WEIGHTING;
            }
        } else if((parentDicNode != nullptr && parentDicNode->getNodeCodePoint() == codePoint) || dicNode->isZeroCostOmission() || dicNode->canBeIntentionalOmission()) {
            return 0.0f;
        } else { // Add middle points
            const int inputIndex = dicNode->getInputIndex(0);
            const int swipeLength = traverseSession->getInputSize();

            int minEdgeIndex = -1;
            float minEdgeDistance = MAX_VALUE_FOR_WEIGHTING;
            bool found = false;
            bool headedTowardsCharacterYet = false;

            const float keyThreshold = (80.0f * distanceThreshold);

            //AKLOGI("commence search for %c", (char)codePoint);
            for (int i = inputIndex; i < swipeLength; i++) {
                if (i == 0) continue;

                const float distance = util::getDistanceLine(traverseSession, codePoint, i - 1, i);

                //AKLOGI("[%c:%d] distance %.2f, min %.2f. thresh %.2f", (char)codePoint, i, distance, minEdgeDistance, keyThreshold);
                if (distance < minEdgeDistance) {
                    if(minEdgeIndex != -1) headedTowardsCharacterYet = true;
                    minEdgeDistance = distance;
                    minEdgeIndex = i;
                }

                if (((distance > minEdgeDistance) || (i >= (swipeLength - 1))) && (minEdgeDistance < keyThreshold) && headedTowardsCharacterYet) {
                    //AKLOGI("found!");
                    found = true;
                    break;
                }
            }

            if(found && parentDicNode != nullptr && minEdgeDistance < MAX_VALUE_FOR_WEIGHTING) {
                float totalDistance = 24.0f * pow(minEdgeDistance, 1.6f);

                const int codePoint0 = parentDicNode->getNodeCodePoint();
                const int codePoint1 = codePoint;

                const int lowerLimit = inputIndex;
                const int upperLimit = minEdgeIndex;

                const float threshold = (distanceThreshold * 86.0f);

                const float punishment = util::calcLineDeviationPunishment(
                        traverseSession, codePoint0, codePoint1, lowerLimit, upperLimit, threshold);

                if(punishment >= MAX_VALUE_FOR_WEIGHTING) {
                    //AKLOGI("Culled due to too large distance (%.2f, %.2f)", totalDistance, punishment);
                    //dicNode->dump("CULLED");
                    return MAX_VALUE_FOR_WEIGHTING;
                }

                totalDistance += punishment;

                inputStateG->mNeedsToUpdateInputStateG = true;
                inputStateG->mInputIndex = minEdgeIndex;
                inputStateG->mRawLength = totalDistance;

                return totalDistance;
            } else {
                //AKLOGI("Culled due to not found or nullptr parent %p %d %.2f. inputIndex is %d and swipeLength is %d", parentDicNode, found, minEdgeDistance, inputIndex, swipeLength);
                //dicNode->dump("CULLED");
            }

            if(parentDicNode == nullptr) {
                AKLOGE("Nullptr parent unexpected! for match");
            }
        }

        return MAX_VALUE_FOR_WEIGHTING;
    }

    AK_FORCE_INLINE bool isProximityDicNode(const DicTraverseSession *const traverseSession,
                            const DicNode *const dicNode) const override {
        return false;
    }

    AK_FORCE_INLINE float getTranspositionCost(const DicTraverseSession *const traverseSession,
                               const DicNode *const parentDicNode, const DicNode *const dicNode) const override {
        return MAX_VALUE_FOR_WEIGHTING;
    }

    AK_FORCE_INLINE float getTransitionCost(const DicTraverseSession *const traverseSession,
                            const DicNode *const dicNode) const override {
        int idx = dicNode->getInputIndex(0);
        if(true || idx < 0 || idx >= traverseSession->getProximityInfoState(0)->size())
            return MAX_VALUE_FOR_WEIGHTING;
        return 1.0f * traverseSession->getProximityInfoState(0)->getProbability(idx, NOT_AN_INDEX);
    }

    AK_FORCE_INLINE float getInsertionCost(const DicTraverseSession *const traverseSession,
                           const DicNode *const parentDicNode, const DicNode *const dicNode) const override {
        return MAX_VALUE_FOR_WEIGHTING;
    }

    AK_FORCE_INLINE float getSpaceOmissionCost(const DicTraverseSession *const traverseSession,
                               const DicNode *const dicNode, DicNode_InputStateG *const inputStateG) const override {
        return MAX_VALUE_FOR_WEIGHTING;// ScoringParams::SPACE_OMISSION_COST;
    }

    AK_FORCE_INLINE float getNewWordBigramLanguageCost(const DicTraverseSession *const traverseSession,
                                       const DicNode *const dicNode, MultiBigramMap *const multiBigramMap) const override {
        return DicNodeUtils::getBigramNodeImprobability(
                traverseSession->getDictionaryStructurePolicy(),
                dicNode, multiBigramMap) * ScoringParams::DISTANCE_WEIGHT_LANGUAGE;
    }

    AK_FORCE_INLINE float getCompletionCost(const DicTraverseSession *const traverseSession,
                            const DicNode *const dicNode) const override {
        return MAX_VALUE_FOR_WEIGHTING;// ScoringParams::COST_COMPLETION;
    }

    AK_FORCE_INLINE float getTerminalInsertionCost(const DicTraverseSession *const traverseSession,
                                   const DicNode *const dicNode) const override {
        return ScoringParams::TERMINAL_INSERTION_COST;
    }

    AK_FORCE_INLINE float getTerminalLanguageCost(const DicTraverseSession *const traverseSession,
                                  const DicNode *const dicNode, float dicNodeLanguageImprobability) const override {
        //return dicNodeLanguageImprobability * ScoringParams::DISTANCE_WEIGHT_LANGUAGE;
        //return //dicNode->getSpatialDistanceForScoring() * dicNodeLanguageImprobability * ScoringParams::DISTANCE_WEIGHT_LANGUAGE;
        return dicNodeLanguageImprobability;
    }

    AK_FORCE_INLINE bool needsToNormalizeCompoundDistance() const override {
        return false;
    }

    AK_FORCE_INLINE float getAdditionalProximityCost() const override {
        return MAX_VALUE_FOR_WEIGHTING;// ScoringParams::ADDITIONAL_PROXIMITY_COST;
    }

    AK_FORCE_INLINE float getSubstitutionCost() const override {
        return MAX_VALUE_FOR_WEIGHTING;
    }

    AK_FORCE_INLINE float getSpaceSubstitutionCost(const DicTraverseSession *const traverseSession,
                                   const DicNode *const dicNode) const override {
        return 1.5f;
    }

    AK_FORCE_INLINE ErrorTypeUtils::ErrorType getErrorType(const CorrectionType correctionType,
                                           const DicTraverseSession *const traverseSession, const DicNode *const parentDicNode,
                                           const DicNode *const dicNode) const override {
        return ErrorTypeUtils::PROXIMITY_CORRECTION;
    }

private:
    DISALLOW_COPY_AND_ASSIGN(SwipeWeighting);
    static const SwipeWeighting sInstance;

    SwipeWeighting() {}
    ~SwipeWeighting() {}
};
};