package uk.co.drnaylor.minecraft.hammer.core;

public class HammerUtility {
    public static String createTimeStringFromOffset(long timeOffset) {
        long time = timeOffset / 1000;
        long sec = time % 60;
        long min = (time / 60) % 60;
        long hour = (time / 3600) % 24;
        long day = time / 86400;

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append(" days");
        }

        if (hour > 0) {
            appendComma(sb);
            sb.append(hour).append(" hours");
        }

        if (min > 0) {
            appendComma(sb);
            sb.append(min).append(" minutes");
        }

        if (sec > 0) {
            appendComma(sb);
            sb.append(sec).append(" seconds");
        }

        if (sb.length() > 0) {
            return sb.toString();
        } else {
            return "unknown";
        }
    }

    private static void appendComma(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(", ");
        }
    }
}
