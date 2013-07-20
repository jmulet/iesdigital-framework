/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.iesapp.framework.util;

/**
 *
 * @author Josep
 */
public class ColorUtils {
  public static java.awt.Color fade(java.awt.Color col)
  {
      double factor = 0.7;
      int r = (int) (factor*col.getRed());
      int g = (int) (factor*col.getGreen());
      int b = (int) (factor*col.getBlue());

      return new java.awt.Color(r,g,b);
  }

  public static java.awt.Color getRGB(String text)
  {
         java.awt.Color color = null;
         text = text.trim();
         int i0;
         i0 = text.indexOf(',');
         String r = text.substring(0,i0);
         text = text.substring(i0+1, text.length());
         i0 = text.indexOf(',');
         String g = text.substring(0,i0);
         String b = text.substring(i0+1, text.length());

         int red = (int) Double.parseDouble(r);
         int green = (int) Double.parseDouble(g);
         int blue = (int) Double.parseDouble(b);

         color = new java.awt.Color(red,green,blue);
         return color;
  }
  
  public static String getString(java.awt.Color color)
  {
      String str = "rgb("+color.getRed()+","+color.getGreen()+","+color.getBlue()+")";
      return str;
  }
}
