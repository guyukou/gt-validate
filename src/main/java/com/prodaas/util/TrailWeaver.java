package com.prodaas.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author guyu
 */
public class TrailWeaver {
    public static String getTrail(int deltaX) {
        if (shouldDo(50)) {
            deltaX += getRandomInt(5) - 2;
        }
        int originDeltaX = deltaX;
        if (deltaX < 30 || deltaX > 200) {
            throw new IllegalArgumentException("fullDeltaX outOf bound: " + deltaX);
        }
        boolean needMoreTrail = shouldDo(70) && deltaX < 185;
        // 增加deltaX
        if (needMoreTrail) {
            deltaX += getRandomInt(2, 10);
        }
        List<int[]> startTrail = generateStartTrail();// 1. 开始轨迹

        List<int[]> endTrail;
        if (needMoreTrail && shouldDo(50)) {
            endTrail = new ArrayList<>();
        } else {
            endTrail = generateEndTrail();
        }
        int startTrailDeltaXSum = sumDeltaX(startTrail) - startTrail.get(0)[0];
        int endTrailDeltaXSum = sumDeltaX(endTrail);
        int middleTrailDeltaXSum = deltaX - startTrailDeltaXSum - endTrailDeltaXSum;

        List<int[]> middleTrail = generateMiddleTrail2(middleTrailDeltaXSum, deltaX);
        List<int[]> result = new ArrayList<>(startTrail);
        result.addAll(middleTrail);
        result.addAll(endTrail);


        // 来回校正
        if (!needMoreTrail) {
            return array2String(result);
        }
        int realDeltaX = 0;
        for (int[] arr : result) {
            realDeltaX += arr[0];
        }
        realDeltaX -= result.get(0)[0];

        int fixTimes = getRandomInt(3) + 1;
        for (int i = 1; i <= fixTimes; i++) {
            int fixDeltaX = Math.abs(realDeltaX - originDeltaX);
            if (i != fixTimes) {
                fixDeltaX += getRandomInt(1, 7);
            }
            result.addAll(generateFixDeltaXes(fixDeltaX, i % 2 == 0));
            if (i % 2 == 0) {
                realDeltaX += fixDeltaX;
            } else {
                realDeltaX -= fixDeltaX;
            }
        }

        realDeltaX = 0;
        for (int[] arr : result) {
            realDeltaX += arr[0];
        }

        return array2String(result);
    }

    private static List<int[]> generateFixDeltaXes(int fixDeltaX, boolean isPositive) {
        List<int[]> result = new ArrayList<>();
        boolean isFirst = true;
        while (fixDeltaX > 0) {
            int endX = isPositive ? getEndX() : -getEndX();
            int endT = getEndT();
            if (shouldDo(15)) {
                endX = (3 - Math.abs(endX));
                if (!isPositive) {
                    endX = -endX;
                }
            }
            if (endX > fixDeltaX) {
                endX = fixDeltaX;
            }
            fixDeltaX -= Math.abs(endX);
            int endY = shouldDo(22) ? getRandomInt(5) - 2 : 0;
            if (isFirst) {
                endT += getRandomInt(200, 400);
            }
            result.add(new int[]{endX, endY, endT});
            isFirst = false;
        }
        result.add(new int[]{0, 0, getRandomInt(200, 400)});
        return result;
    }


    private static String array2String(List<int[]> result) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int[] arr : result) {
            if (sb.length() != 1) {
                sb.append(",");
            }
            sb.append("[");
            sb.append(arr[0]);
            sb.append(",");
            sb.append(arr[1]);
            sb.append(",");
            sb.append(arr[2]);
            sb.append("]");
        }

        sb.append("]");
        return sb.toString();
    }

    private static List<int[]> generateStartTrail() {
        List<int[]> result = new ArrayList<>();
        result.add(new int[]{16 + getRandomInt(10), 16 + getRandomInt(10), 0});
        result.add(new int[]{2, 0, getRandomInt(100, 150)});
        if (shouldDo(50)) {
            result.add(new int[]{2, 0, getRandomInt(15, 19)});
        }
        return result;
    }

    private static List<int[]> generateMiddleTrail(int middleTrailDeltaXSum, int deltaX) {
        List<int[]> result = new ArrayList<>();
        List<Integer> sections = new ArrayList<>();
        Integer section;
        while ((section = generateDeltaXSection(deltaX)) < middleTrailDeltaXSum) {
            sections.add(section);
            middleTrailDeltaXSum -= section;
        }
        sections.add(middleTrailDeltaXSum);

        List<Integer> subSecs = new ArrayList<>();
        int minSec = deltaX < 70 ? 1 : deltaX < 120 ? 2 : 3;
        for (Integer sec : sections) {
            int con = 0;
            int firstSec = shouldDo(30) ? sec / 2 : sec / 3;
            sec -= firstSec;
            subSecs.add(firstSec);
            int lastSec = firstSec;
            while (true) {
                int randomInt = shouldDo(16) ? 0 : getRandomInt(1, 3);
                int currSec = lastSec - randomInt;
                // randomInt == 0并且连续超过2，需要微调
                if (randomInt == 0) {
                    con++;
                    if (con == 2 || currSec >= 6) {
                        con = 0;
                        currSec -= getRandomInt(1, 3);
                    }
                }
                // currSec < minSec 需要设置下限
                if (currSec < minSec) {
                    currSec = minSec;
                }
                if (currSec > sec) {
                    currSec = sec;
                }
                subSecs.add(currSec);
                lastSec = currSec;
                sec -= currSec;
                if (sec == 0) {
                    break;
                }
            }
        }
        int length = subSecs.size();
        List<Integer> deltaYs = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            deltaYs.add(shouldDo(20) ? getRandomInt(5) - 2 : 0);
        }


        List<Integer> deltaTs = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            boolean shouldStopMore = shouldDo(15);
            if (shouldStopMore) {
                deltaTs.add(getRandomInt(40) + 20);
            } else {
                deltaTs.add(getRandomInt(15, 19));
            }
        }
        boolean shouldStop = shouldDo(30);
        if (shouldStop) {
            int randomInt = getRandomInt(deltaTs.size() / 4, deltaTs.size() * 4 / 5);
            deltaTs.set(randomInt, getRandomInt(150, 500));
        }
        for (int i = 0; i < length; i++) {
            result.add(new int[]{subSecs.get(i), deltaYs.get(i), deltaTs.get(i)});
        }


        return result;
    }

    private static List<int[]> generateMiddleTrail2(int middleTrailDeltaXSum, int fullDeltaX) {
        List<int[]> result = new ArrayList<>();
        if (fullDeltaX <= 71) {
            int length = middleTrailDeltaXSum;
            List<Integer> deltaXArray = new ArrayList<>(length);
            Random rand = new Random();
            int i = 3 + rand.nextInt(1);
            for (int j = 0; j < length; j++) {
                deltaXArray.add(1);
            }
            while (i < length) {
                int j = rand.nextInt(100);
                int num = j < 5 ? 3 : 2;
                deltaXArray.set(i, num);
                i += 3 + rand.nextInt(1);
            }
            int sum = 0;
            for (int i1 : deltaXArray) {
                sum += i1;
            }

            while (sum > middleTrailDeltaXSum) {
                sum -= deltaXArray.get(deltaXArray.size() - 1);
                deltaXArray = deltaXArray.subList(0, deltaXArray.size() - 1);
            }

            int slot = middleTrailDeltaXSum - sum;
            for (int j = 0; j < slot; j++) {
                deltaXArray.add(1);
            }



            List<Integer> deltaTBody = new ArrayList<>();
            int m = 0;
            while (m < middleTrailDeltaXSum) {
                int i1 = 2 + rand.nextInt(1);
                for (int j = 0; j < i1; j++) {
                    deltaTBody.add(18 + rand.nextInt(2));
                }
                deltaTBody.add(30 + rand.nextInt(20));
                m += i1 + 1;
            }
            for (int j = 0; j < deltaXArray.size(); j++) {
                int deltaY = shouldDo(20) ? getRandomInt(-2, 3) : 0;
                result.add(new int[]{deltaXArray.get(j), deltaY, deltaTBody.get(j)});
            }


        } else if (fullDeltaX <= 130) {
            List<Integer> deltaXMiddleArray = new ArrayList<>();
            List<Integer> deltaTMiddleArray = new ArrayList<>();
            int deltaXMiddleSum = middleTrailDeltaXSum;
            while (deltaXMiddleSum > 10) {
                int aDeltaX = rand.nextInt(10) < 4 ? 1 : 2;
                deltaXMiddleArray.add(aDeltaX);
                deltaXMiddleSum -= aDeltaX;

                aDeltaX += rand.nextInt(1);
                deltaXMiddleSum -= aDeltaX;
                deltaXMiddleArray.add(aDeltaX);

                aDeltaX += rand.nextInt(10) < 7 ? 2 : 1;
                deltaXMiddleSum -= aDeltaX;
                deltaXMiddleArray.add(aDeltaX);

                if (rand.nextInt(10) < 3) {
                    aDeltaX += 1 + rand.nextInt(1);
                    deltaXMiddleSum -= aDeltaX;
                    deltaXMiddleArray.add(aDeltaX);
                }
            }
            boolean first = true;
            for (int i = 0; i < deltaXMiddleArray.size(); i++) {
                if (deltaXMiddleArray.get(i) == 4) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    int start = i;
                    while (deltaXMiddleSum-- > 0) {
                        deltaXMiddleArray.set(start, deltaXMiddleArray.get(start) + 1);
                        start++;
                    }
                }
            }
            for (int i = 0; i < deltaXMiddleArray.size(); i++) {
                int time = 16 + rand.nextInt(1);
                int random = rand.nextInt(20);
                if (random < 3) {
                    time -= 1;
                } else if (random > 17) {
                    time += 1;
                }
                deltaTMiddleArray.add(time);
            }
            for (int j = 0; j < deltaXMiddleArray.size(); j++) {
                int deltaY = shouldDo(20) ? getRandomInt(-2, 3) : 0;
                result.add(new int[]{deltaXMiddleArray.get(j), deltaY, deltaTMiddleArray.get(j)});
            }

        } else {
            List<Integer> deltaXMiddleArray = new ArrayList<>();
            List<Integer> deltaTMiddleArray = new ArrayList<>();
            int deltaXMiddleSum = middleTrailDeltaXSum;
            while (deltaXMiddleSum > 15) {
                int aDeltaX = rand.nextInt(20) < 3 ? 3 : 2;
                deltaXMiddleArray.add(aDeltaX);
                deltaXMiddleSum -= aDeltaX;

                aDeltaX += rand.nextInt(20) < 5 ? 1 : 2;
                deltaXMiddleSum -= aDeltaX;
                deltaXMiddleArray.add(aDeltaX);

                aDeltaX += rand.nextInt(10) < 7 ? 2 : 1;
                deltaXMiddleSum -= aDeltaX;
                deltaXMiddleArray.add(aDeltaX);

                if (rand.nextInt(10) < 7) {
                    aDeltaX += 1;
                    deltaXMiddleSum -= aDeltaX;
                    deltaXMiddleArray.add(aDeltaX);
                }
            }
            boolean first = true;
            for (int i = 0; i < deltaXMiddleArray.size(); i++) {
                if (deltaXMiddleArray.get(i) == 2) {
                    if (first) {
                        first = false;
                        continue;
                    }
                    int start = i;
                    while (deltaXMiddleSum-- > 0) {
                        deltaXMiddleArray.set(start, deltaXMiddleArray.get(start) + 1);
                        start++;
                    }
                }
            }
            for (int i = 0; i < deltaXMiddleArray.size(); i++) {
                int time = 16 + rand.nextInt(1);
                int random = rand.nextInt(20);
                if (random < 3) {
                    time -= 1;
                } else if (random > 17) {
                    time += 1;
                }
                deltaTMiddleArray.add(time);
            }

            for (int j = 0; j < deltaXMiddleArray.size(); j++) {
                int deltaY = shouldDo(20) ? getRandomInt(-2, 3) : 0;
                result.add(new int[]{deltaXMiddleArray.get(j), deltaY, deltaTMiddleArray.get(j)});
            }

        }
        return result;
    }


    private static List<int[]> generateEndTrail() {
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < getRandomInt(5, 9); i++) {
            result.add(new int[]{getEndX(), getDeltaY(20), 20 + getRandomInt(60)});
        }
        result.add(new int[]{0, 0, 80 + getRandomInt(100)});

        return result;
    }

    private static int sumDeltaX(List<int[]> startTrail) {
        int sum = 0;
        for (int[] array : startTrail) {
            sum += array[0];
        }
        return sum;
    }

    private static Random rand = new Random();

    private static boolean shouldDo(int percentage) {
        return rand.nextInt(100) < percentage;
    }

    private static int getRandomInt(int start, int end) {
        return rand.nextInt(end - start) + start;
    }

    private static int getRandomInt(int range) {
        return rand.nextInt(range);
    }

    private static int getDeltaY(int rate) {
        if (rate > 100) {
            rate = rate % 100;
        }
        if (shouldDo(rate)) {
            return getRandomInt(5) - 2;
        }
        return 0;
    }

    private static int generateDeltaXSection(int deltaX) {
        if (deltaX < 70) {
            int randomInt = getRandomInt(100);
            if (randomInt < 6) {
                return 4;
            } else if (randomInt < 24) {
                return 6;
            } else if (randomInt < 30) {
                return 7;
            } else if (randomInt < 42) {
                return 8;
            } else if (randomInt < 44) {
                return 9;
            } else if (randomInt < 64) {
                return 10;
            } else if (randomInt < 66) {
                return 11;
            } else if (randomInt < 74) {
                return 12;
            } else if (randomInt < 78) {
                return 13;
            } else if (randomInt < 84) {
                return 14;
            } else if (randomInt < 92) {
                return 16;
            }
            return 18;
        } else if (deltaX < 120) {
            int randomInt = getRandomInt(100);
            if (randomInt < 5) {
                return 4;
            } else if (randomInt < 12) {
                return 5;
            } else if (randomInt < 21) {
                return 6;
            } else if (randomInt < 26) {
                return 7;
            } else if (randomInt < 51) {
                return 8;
            } else if (randomInt < 55) {
                return 9;
            } else if (randomInt < 63) {
                return 10;
            } else if (randomInt < 66) {
                return 11;
            } else if (randomInt < 75) {
                return 12;
            } else if (randomInt < 77) {
                return 13;
            } else if (randomInt < 83) {
                return 14;
            } else if (randomInt < 94) {
                return 16;
            } else if (randomInt < 96) {
                return 18;
            }
            return 20;
        }
        int randomInt = getRandomInt(178);

        if (randomInt < 5) {
            return 3;
        } else if (randomInt < 13) {
            return 4;
        } else if (randomInt < 17) {
            return 5;
        } else if (randomInt < 30) {
            return 6;
        } else if (randomInt < 38) {
            return 7;
        } else if (randomInt < 70) {
            return 8;
        } else if (randomInt < 75) {
            return 9;
        } else if (randomInt < 88) {
            return 10;
        } else if (randomInt < 95) {
            return 11;
        } else if (randomInt < 111) {
            return 12;
        } else if (randomInt < 117) {
            return 13;
        } else if (randomInt < 139) {
            return 14;
        } else if (randomInt < 147) {
            return 15;
        } else if (randomInt < 165) {
            return 16;
        } else if (randomInt < 169) {
            return 17;
        } else if (randomInt < 173) {
            return 18;
        }
        return 19;
    }

    private static int[] endXes = new int[]{1, 1, 2};
    private static int endXIndex = 0;

    private static int getEndX() {
        endXIndex++;
        if (endXIndex == 3) {
            endXIndex = 0;
        }
        return endXes[endXIndex];
    }

    private static int endTIndex = 0;

    private static int getEndT() {
        endTIndex++;
        if (endTIndex == 3) {
            endTIndex = 0;
        }
        if (endTIndex == 0) {
            return getRandomInt(40, 90);
        } else if (endTIndex == 1) {
            return getRandomInt(20, 40);
        }
        return getRandomInt(80, 150);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            String trail = getTrail(150);
            System.out.println(trail);
        }
    }


}
