package org.menthal.model.implicits

import org.joda.time.DateTime

/**
 * Created by konrad on 21.07.2014.
 */
object EventImplicts {
  implicit def dateToLong(dt:DateTime):Long = dt.getMillis
  //implicit def longToDate(ln: Long): DateTime = longToDate(ln)
}
