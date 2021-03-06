package com.shellshellfish.aaas.common.utils;


import static java.lang.Math.abs;

import com.google.common.hash.Hashing;
import com.shellshellfish.aaas.common.enums.TrdOrderStatusEnum;
import com.shellshellfish.aaas.common.enums.TrdZZCheckStatusEnum;
import com.shellshellfish.aaas.common.enums.ZZKKStatusEnum;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import org.springframework.util.StringUtils;


public class TradeUtil {
  private static String generateOrderId(Long bankId, int tradeBrokerId ){
    Long utcTime = getUTCTime();
    System.out.println(utcTime);
    StringBuilder sb = new StringBuilder();
    sb.append(bankId).append(String.format("%04d", tradeBrokerId)).append(String.format("%018d", utcTime));
    return sb.toString();
  }

  public static String generateOrderIdByBankCardNum(String bankCardNum, int tradeBrokerId){
    Long utcTime = getUTCTime();
    System.out.println(utcTime);
    String disOrderedBankCardId = bankCardNum.substring(0, 6) + bankCardNum.substring(bankCardNum
        .length() -4);
    StringBuilder sb = new StringBuilder();
    sb.append(disOrderedBankCardId).append(String.format("%02d", tradeBrokerId)).append(String
        .format("%015d", utcTime));
    return sb.toString();
  }

  public static String getReadableDateTime(Long utcTime){
    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(utcTime), ZoneId.systemDefault
        ());
    return     zonedDateTime.toLocalDateTime().toString();
  }

  public static Long getUTCOfSpecificTimeToday(int hour, int minute){
    String day = getReadableDateTime(getUTCTime()).split("T")[0];
    day = day +  " " + String.format("%02d",hour)+":"+String.format("%02d", minute)+ ":00";
    ZonedDateTime dt = ZonedDateTime.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault()));
    return dt.toInstant().toEpochMilli();
  }

  public static Date getDateOfSpecificTimeToday(int hour, int minute){
    String day = getReadableDateTime(getUTCTime()).split("T")[0];
    day = day +  " " + String.format("%02d",hour)+":"+String.format("%02d", minute)+ ":00";
    ZonedDateTime dt = ZonedDateTime.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault()));
    Date out = Date.from(dt.toInstant());
    return out;
  }

  public static Date getDateOfSpecificTime(int year, int month, int day, int hour, int minute){

    String time = year +  "-" +String.format("%02d",month) + "-" +String.format("%02d",day) +" "+
        String.format("%02d", hour)+":"+String.format("%02d", minute)+ ":00";
    ZonedDateTime dt = ZonedDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault()));
    Date out = Date.from(dt.toInstant());
    return out;
  }



  public static Long getUTCTimeOfSpecificTime(int year, int month, int day, int hour, int
      minute){

    String time = year +  "-" +String.format("%02d",month) + "-" +String.format("%02d",day) +" "+
        String.format("%02d", hour)+":"+String.format("%02d", minute)+ ":00";
    ZonedDateTime dt = ZonedDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault()));
    Date out = Date.from(dt.toInstant());
    return out.toInstant().toEpochMilli();
  }
  public static Long getUTCTime(){
    ZonedDateTime utcDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
    return utcDateTime.toInstant().toEpochMilli();
  }

  public static Long getUTCTime1HourBefore(){

    ZonedDateTime utcDateTime = ZonedDateTime.of(LocalDateTime.now().plusHours(-1), ZoneId.systemDefault());
    return utcDateTime.toInstant().toEpochMilli();

  }

  public static Long getUTCTimeHoursBefore(int hours){
    int absHours = abs(hours);
    ZonedDateTime utcDateTime = ZonedDateTime.of(LocalDateTime.now().plusHours(-absHours), ZoneId.systemDefault());
    return utcDateTime.toInstant().toEpochMilli();
  }

  public static Long getUTCTime1DayBefore(){
    ZonedDateTime utcDateTime = ZonedDateTime.of(LocalDateTime.now().plusDays(-1), ZoneId.systemDefault());
    return utcDateTime.toInstant().toEpochMilli();
  }

  public static Long getUTCTimeNDayAfter(int nDays){
    ZonedDateTime utcDateTime = ZonedDateTime.of(LocalDateTime.now().plusDays(nDays), ZoneId.systemDefault());
    return utcDateTime.toInstant().toEpochMilli();
  }

  public static Long getUTCTimeNDayAfter(Long startUtcTime,int nDays){
    ZonedDateTime zonedDateTime =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(startUtcTime), ZoneId.systemDefault
        ());
    ZonedDateTime utcDateTime = ZonedDateTime.of(zonedDateTime.plusDays(nDays).toLocalDateTime(), ZoneId
    .systemDefault());
    return utcDateTime.toInstant().toEpochMilli();
  }

  public static ZonedDateTime getUTCDateNDayAfter(Long startUtcTime,int nDays){
    ZonedDateTime zonedDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(startUtcTime), ZoneId.systemDefault
            ());
    ZonedDateTime utcDateTime = ZonedDateTime.of(zonedDateTime.plusDays(nDays).toLocalDateTime(), ZoneId
        .systemDefault());
    return utcDateTime;
  }

  public static Long getUTCTimeTodayStartTime(String zoneId){
    long utcDateStartTimeOffset = ZonedDateTime.now(ZoneId.of(zoneId)).toOffsetDateTime().toInstant
        ().toEpochMilli();
    System.out.println("utcDateStartTime：" + utcDateStartTimeOffset);
    ZonedDateTime utcDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
    long currentUTCtime = utcDateTime.toInstant().toEpochMilli();
    System.out.println("currentUTCtime：" + currentUTCtime);

    return currentUTCtime - utcDateStartTimeOffset;
  }

  public static void main(String[] argv){
    TimeZone timeZone = TimeZone.getTimeZone("UTC");
    Calendar calendar = Calendar.getInstance(timeZone);
    calendar.set(9999,12,31);
    System.out.println(calendar.getTimeInMillis());

    Long num1 = 12126212L;
    Long num2 = 10000L;
    System.out.println(num1/num2);

  }

  /**
   * 输入为有小数点的数字的字符串
   * 输出为精确到百分之一的整型
   * @param originNum
   * @return
   */
  public static Long getLongNumWithMul100(String originNum){

      BigDecimal origBigD = new BigDecimal(originNum);
      return  origBigD.multiply(BigDecimal.valueOf(100)).longValue();

  }

  /**
   * 输入为有小数点的数字的BigDecimal
   * 输出为精确到百分之一的整型
   * @param originNum
   * @return
   */
  public static Long getLongNumWithMul100(BigDecimal originNum){
    return  originNum.multiply(BigDecimal.valueOf(100)).longValue();
  }

  public static Long getLongNumWithMul1000000(Double originNum){
    Double result = originNum*1000000;
    return result.longValue();
  }
  //originNavadj is 1000000 times origin navadj
  public static BigDecimal getNavadjByLongOrigin(Long navadj){
    return MathUtil.round(BigDecimal.valueOf(navadj).divide(BigDecimal.valueOf(1000000)),6,true);
  }

  /**
   *
   * @param originNum
   * @return
   */
  public static double getDoubleWithDiv100(Long originNum){
    return Math.round(originNum/100.0);
  }

  public static BigDecimal getBigDecimalNumWithDiv100(Long originNum){
    return MathUtil.round(BigDecimal.valueOf(originNum).divide(BigDecimal.valueOf(100)),2,true);
  }

  public static Long getLongWithDiv(Long originNum, Long divider){
    int sign = (originNum > 0 ? 1 : -1) * (divider > 0 ? 1 : -1);
    return sign * (abs(originNum) + abs(divider) - 1) / abs(divider);
  }

  public static BigDecimal getBigDecimalNumWithDivOfTwoLongAndRundDown(Long number, Long divider){
    return BigDecimal.valueOf(number).divide(BigDecimal.valueOf(divider),2,RoundingMode.HALF_DOWN);
  }

  public static BigDecimal getBigDecimalNumWithDivOfTwoLongAndRundUp(Long number, Long divider){
    return BigDecimal.valueOf(number).divide(BigDecimal.valueOf(divider),2,RoundingMode.HALF_UP);
  }

  public static BigDecimal getBigDecimalNumWithDivOfTwoLong(Long number, Long divider){
    return MathUtil.round(BigDecimal.valueOf(number).divide(BigDecimal.valueOf(divider)),2,true);
  }

  public static BigDecimal getBigDecimalNumWithRoundUp2Digit(String origin){
    return MathUtil.round(new BigDecimal(origin),2,true);
  }
  public static BigDecimal getBigDecimalNumWithRoundUpNDigit(String origin, int n){
    return MathUtil.round(new BigDecimal(origin),n,true);
  }

  public static BigDecimal getBigDecimalNumWithDiv10000(Long originNum){
    return new BigDecimal(originNum).divide(BigDecimal.valueOf(10000));
  }

  public static Long getLongFromNumWithDiv10000(Long originNum){
    BigDecimal result =  new BigDecimal(originNum).divide(BigDecimal.valueOf(10000));
    result = round(result, 0, true);
    return result.longValue();
  }

  public static BigDecimal round(BigDecimal d, int scale, boolean roundUp) {
    int mode = (roundUp) ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
    return d.setScale(scale, mode);
  }

  public static BigDecimal getBigDecimalNumWithDiv10000(Integer originNum){


    return new BigDecimal(originNum).divide(BigDecimal.valueOf(10000));

  }
  public static int getPayFlowStatus(String kkstat){
    if(kkstat.equals(""+ZZKKStatusEnum.KKSUCCESS.getStatus())){
      return TrdOrderStatusEnum.PAYWAITCONFIRM.getStatus();
    }else if(kkstat.equals(""+ZZKKStatusEnum.WAITCONFIRM.getStatus())){
      return TrdOrderStatusEnum.WAITPAY.getStatus();
    }else{
      return TrdOrderStatusEnum.FAILED.getStatus();
    }

//    if(kkstat.equals(TrdZZCheckStatusEnum.CONFIRMSUCCESS) || kkstat.equals(TrdZZCheckStatusEnum.REALTIMECONFIRMSUCESS)){
//      return TrdOrderStatusEnum.CONFIRMED.getStatus();
//    }else if(kkstat.equals(TrdZZCheckStatusEnum.CONFIRMFAILED) || kkstat.equals
//        (TrdZZCheckStatusEnum.NOTHANDLED)){
//      return TrdOrderStatusEnum.FAILED.getStatus();
//    }else{
//      return TrdOrderStatusEnum.PAYWAITCONFIRM.getStatus();
//    }
  }

  public static String getSHA256encoding(String originStr){
    String sha256hex = Hashing.sha256()
        .hashString(originStr, StandardCharsets.UTF_8)
        .toString();
    return sha256hex;
  }

  public static String getZZOpenId(String personId){
    String sha256hex = getSHA256encoding(personId+"shellshellfish");
    return sha256hex;
  }

  /**
   * 用orderDetail的主键id结合 orderId 构成给中证接口购买时用的outside orderNo
   * 规则为orderId+ %08d orderDetail id
   */

  public static String getZZOutsideOrderNo(String orderId, Long orderDetailId){
    return orderId+String.format("08d%", orderDetailId);
  }
  public static Long getUTCTimeInSeconds(){
    ZonedDateTime utcDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
    return utcDateTime.toInstant().getEpochSecond();
  }


  /**
   * 获得某一年的周末判断静态map
   * @param startTime
   * @return
   */
  public static String getTplusNDayOfWork(Long startTime, int n){

    ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault
        ());
    Long actualDaysPlus ;

    LocalDateTime localDateTimeLimit = LocalDateTime.of(zonedDateTime.toLocalDate(), LocalTime.of
        (15, 0));
    if(zonedDateTime.toLocalDateTime().toInstant(ZoneOffset.UTC).getEpochSecond() > localDateTimeLimit
        .toInstant(ZoneOffset.UTC).getEpochSecond()){
      System.out.println("need add extra 1 day because it is after 15:00");
      actualDaysPlus = getActualNumberOfDaysToAdd(n+1, zonedDateTime.toLocalDate().getDayOfWeek()
          .getValue());
    }else{
      actualDaysPlus = getActualNumberOfDaysToAdd( n, zonedDateTime.toLocalDate().getDayOfWeek()
          .getValue());
    }
    ZonedDateTime utcDateTime = ZonedDateTime.of(zonedDateTime.plusDays(actualDaysPlus).toLocalDateTime(), ZoneId
        .systemDefault());
    return utcDateTime.toLocalDateTime().toString();

  }

  public static long getActualNumberOfDaysToAdd(long workdays, int dayOfWeek) {
    if (dayOfWeek < 6) { // date is a workday
      return workdays + (workdays + dayOfWeek - 1) / 5 * 2;
    } else { // date is a weekend
      return workdays + (workdays - 1) / 5 * 2 + (7 - dayOfWeek);
    }
  }

  public static Set<String> getSetFromString(String origin, String sep){
    if(StringUtils.isEmpty(origin) || StringUtils.isEmpty(sep)){
      throw new IllegalArgumentException("origin:" + origin + " sep:"+sep +" is not valid");
    }
    Set<String> result = new HashSet<String>();
    String[] arrayRlt = origin.split(sep);
    if(arrayRlt.length > 0){
      for(String item: arrayRlt){
        result.add(item);
      }
    }
    return result;
  }

  public static String getOrderIdByOutsideOrderNo(String outsideOrderNo, Long orderDetailId)
      throws Exception {
    if(StringUtils.isEmpty(outsideOrderNo)){
      throw new IllegalAccessException("outsideOrderNo is empty");
    }
    if(orderDetailId <= 0){
      throw new IllegalAccessException("orderDetailId should be greate than 0");
    }
    int lastIdxOfODI =  outsideOrderNo.lastIndexOf(String.valueOf(orderDetailId));
    if( lastIdxOfODI < 0){
      throw new Exception(String.format("outsideOrderNo:%s didn't contains %d", outsideOrderNo,
          orderDetailId));
    }
    return outsideOrderNo.substring(0, lastIdxOfODI);
  }

//  public static Long getLongFromDividByBD(String tradeconfirmsum, String tradeconfirmshare) {
//    BigDecimal result = (new BigDecimal(tradeconfirmsum)).divide(new BigDecimal(tradeconfirmshare));
//    return getLongNumWithMul100(result);
//  }

  /**
   *
   * @param currentTime currentUtcTime
   * @param dayCompare format yyyy-mm-dd
   * @return
   */


  public static Long getDifferentDays(Long currentTime, String dayCompare){
    String currentDay = getReadableDateTime(currentTime).split("T")[0];
    String[] currentTimes = currentDay.split("-");
    Long getStanardTimeOfCurrentDay = getUTCTimeOfSpecificTime(Integer.parseInt(currentTimes[0]),
        Integer.parseInt(currentTimes[1]), Integer.parseInt(currentTimes[2]), 12, 0);
    String[] compareTimes = dayCompare.split("-");
    Long getStanardTimeOfCompareDay = getUTCTimeOfSpecificTime(Integer.parseInt(compareTimes[0]),
        Integer.parseInt(compareTimes[1]), Integer.parseInt(compareTimes[2]), 12, 0);
    Long dayTime  = 24*60*60*1000L;

    Long diffDays =   (getStanardTimeOfCurrentDay - getStanardTimeOfCompareDay)/dayTime;


    return diffDays;


  }

  /**
   * yyyymmdd or yyyy-mm-dd
   * @param dayStub
   * @param daysBefore
   * @return
   */
  public static String getDayBefore(String dayStub, int daysBefore){
    String[] currentTimes = new String[3];
    if(dayStub.contains("-")){
      currentTimes = dayStub.split("-");
    }else{
      currentTimes[0] = dayStub.substring(0,4);
      currentTimes[1] = dayStub.substring(4,6);
      currentTimes[2] = dayStub.substring(6,8);
    }


    Long getStanardTimeOfCurrentDay = getUTCTimeOfSpecificTime(Integer.parseInt(currentTimes[0]),
        Integer.parseInt(currentTimes[1]), Integer.parseInt(currentTimes[2]), 12, 0);
    Long dayTime  = 24*60*60*1000L;
    Long daysBeforeTime = getStanardTimeOfCurrentDay - dayTime*daysBefore;
    String dayWanted = getReadableDateTime(daysBeforeTime).split("T")[0];
    return dayWanted;

  }
  public static String getMD5(String originStr) throws NoSuchAlgorithmException
  {
    MessageDigest messageDigest=MessageDigest.getInstance("MD5");

    messageDigest.update(originStr.getBytes());
    byte[] digest=messageDigest.digest();
    StringBuffer sb = new StringBuffer();
    for (byte b : digest) {
      sb.append(Integer.toHexString((int) (b & 0xff)));
    }
    return sb.toString();
  }

  public static boolean isLatterThan(TrdOrderStatusEnum statusCurr, TrdOrderStatusEnum
      statusCompared){
    if(statusCompared.getStatus() == statusCurr.getStatus()){
      return false;
    }

    if(statusCurr.getStatus() == TrdOrderStatusEnum.CONFIRMED.getStatus()){
      if(statusCompared.getStatus() == TrdOrderStatusEnum.WAITPAY.getStatus()|| statusCompared
          .getStatus() == TrdOrderStatusEnum.PAYWAITCONFIRM.getStatus()){
        return true;
      }
    }
    if(statusCurr.getStatus() == TrdOrderStatusEnum.SELLCONFIRMED.getStatus()){
      if(statusCompared.getStatus() == TrdOrderStatusEnum.WAITSELL.getStatus()|| statusCompared
          .getStatus() == TrdOrderStatusEnum.SELLWAITCONFIRM.getStatus()){
        return true;
      }
    }
    if((statusCurr.getStatus() == TrdOrderStatusEnum.FAILED.getStatus()|| statusCurr.getStatus()
        == TrdOrderStatusEnum.REDEEMFAILED.getStatus()) && statusCompared.getStatus() !=
        statusCurr.getStatus()){
      return true;
    }
    return true;
  }
}
