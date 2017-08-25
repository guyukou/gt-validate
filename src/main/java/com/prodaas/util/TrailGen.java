package com.prodaas.util;

import java.util.*;

/**
 * @author guyu
 */
public class TrailGen {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(generateLongTrail(170));
        }
    }

    public static String generateTrail(int deltaX) {
        while (true) {
            try {
                if (deltaX <= 71) {
                    return generateShortTrail(deltaX);
                } else if (deltaX <= 130) {
                    return generateMiddleTrail(deltaX);
                }
                return generateLongTrail(deltaX);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String generateLongTrail(int deltaX) {
        Random rand = new Random();
        int deltaXStartSize = 6 + rand.nextInt(4);
        int deltaXStartTime = 80 + rand.nextInt(20);
        int deltaXSum = deltaXStartSize;

        int deltaXSlowDownTimes = 2 + rand.nextInt(1);
        List<Integer> deltaXSlowDownArray = new ArrayList<>();
        List<Integer> deltaTSlowDownArray = new ArrayList<>();
        for (int i = 0; i < deltaXSlowDownTimes; i++) {
            deltaXSlowDownArray.add(2);
            int randomDeltaX = rand.nextInt(10) < 3 ? 2 : 1;
            deltaXSlowDownArray.add(randomDeltaX);
            deltaXSlowDownArray.add(1);
            deltaXSum += 3 + randomDeltaX;
            deltaTSlowDownArray.add(30 + rand.nextInt(10));
            deltaTSlowDownArray.add(50 + rand.nextInt(13));
            deltaTSlowDownArray.add(98 + rand.nextInt(12));
        }

        List<Integer> deltaXMiddleArray = new ArrayList<>();
        List<Integer> deltaTMiddleArray = new ArrayList<>();
        int deltaXMiddleSum = deltaX - deltaXSum;
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
        List<Integer> deltaXArray = new ArrayList<>();
        List<Integer> deltaTArray = new ArrayList<>();
        deltaXArray.add(deltaXStartSize);
        deltaTArray.add(deltaXStartTime);
        Collections.reverse(deltaXMiddleArray);
        deltaXArray.addAll(deltaXMiddleArray);
        Collections.reverse(deltaTMiddleArray);
        deltaTArray.addAll(deltaTMiddleArray);
        deltaXArray.addAll(deltaXSlowDownArray);
        deltaTArray.addAll(deltaTSlowDownArray);

        return generateResult(deltaXArray, deltaTArray);
    }

    private static String generateMiddleTrail(int deltaX) {
        Random rand = new Random();
        int deltaXStartSize = 2;
        int deltaXStartTime = 80 + rand.nextInt(20);
        int deltaXSum = deltaXStartSize;

        int deltaXSlowDownTimes = 2 + rand.nextInt(1);
        List<Integer> deltaXSlowDownArray = new ArrayList<>();
        List<Integer> deltaTSlowDownArray = new ArrayList<>();
        for (int i = 0; i < deltaXSlowDownTimes; i++) {
            deltaXSlowDownArray.add(2);
            int randomDeltaX = rand.nextInt(10) < 3 ? 2 : 1;
            deltaXSlowDownArray.add(randomDeltaX);
            deltaXSlowDownArray.add(1);
            deltaXSum += 3 + randomDeltaX;
            deltaTSlowDownArray.add(20 + rand.nextInt(10));
            deltaTSlowDownArray.add(40 + rand.nextInt(13));
            deltaTSlowDownArray.add(78 + rand.nextInt(12));
        }

        List<Integer> deltaXMiddleArray = new ArrayList<>();
        List<Integer> deltaTMiddleArray = new ArrayList<>();
        int deltaXMiddleSum = deltaX - deltaXSum;
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
        List<Integer> deltaXArray = new ArrayList<>();
        List<Integer> deltaTArray = new ArrayList<>();
        deltaXArray.add(deltaXStartSize);
        deltaTArray.add(deltaXStartTime);
        Collections.reverse(deltaXMiddleArray);
        deltaXArray.addAll(deltaXMiddleArray);
        Collections.reverse(deltaTMiddleArray);
        deltaTArray.addAll(deltaTMiddleArray);
        deltaXArray.addAll(deltaXSlowDownArray);
        deltaTArray.addAll(deltaTSlowDownArray);

        return generateResult(deltaXArray, deltaTArray);
    }

    private static String generateShortTrail(int deltaX) {
        int length = (int) (deltaX * 0.8);
        List<Integer> deltaXArray = new ArrayList<>(length);
        Collections.fill(deltaXArray, 1);
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

        while (sum > deltaX) {
            sum -= deltaXArray.get(deltaXArray.size() - 1);
            deltaXArray = deltaXArray.subList(0, deltaXArray.size() - 1);
        }

        int slot = deltaX - sum;
        for (int j = 0; j < slot; j++) {
            deltaXArray.add(1);
        }
        sum = 0;
        for (int i1 : deltaXArray) {
            sum += i1;
        }


        int t1 = 100 + rand.nextInt(50);
        int slowDownTimes = 2 + rand.nextInt(1);
        List<Integer> deltaTTail = new ArrayList<>();
        for (int j = 0; j < slowDownTimes; j++) {
            deltaTTail.add(75 + rand.nextInt(10));
            deltaTTail.add(48 + rand.nextInt(10));
        }
        deltaTTail.add(90 + rand.nextInt(20));
        deltaTTail.add(200 + rand.nextInt(30));

        int deltaTBodySize = deltaXArray.size() - deltaTTail.size() - 1;
        List<Integer> deltaTBody = new ArrayList<>();
        int m = 0;
        while (m < deltaTBodySize) {
            int i1 = 2 + rand.nextInt(1);
            for (int j = 0; j < i1; j++) {
                deltaTBody.add(18 + rand.nextInt(2));
            }
            deltaTBody.add(30 + rand.nextInt(20));
            m += i1 + 1;
        }
        List<Integer> deltaTArray = new ArrayList<>();
        deltaTArray.add(t1);
        deltaTArray.addAll(deltaTBody);
        deltaTArray.addAll(deltaTTail);


        return generateResult(deltaXArray, deltaTArray);
    }

    private static String generateResult(List<Integer> deltaXArray, List<Integer> deltaTArray) {
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(String.format("[%d,%d,%d],", 16 + rand.nextInt(10), 16 + rand.nextInt(10), 0));
        for (int j = 0; j < deltaXArray.size(); j++) {
            int n1 = deltaXArray.get(j);
            int n2 = rand.nextInt(100) < 3 ? rand.nextInt(4) - 2 : 0;
            int n3 = deltaTArray.get(j);
            sb.append(String.format("[%d,%d,%d],", n1, n2, n3));
        }
        sb.replace(sb.length() - 1, sb.length(), "]");

        return sb.toString();
    }
}
