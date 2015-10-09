/*    */ package com.l2fprod.common.model;
/*    */ 
/*    */ import com.l2fprod.common.beans.BeanInfoResolver;
/*    */ import java.beans.BeanInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultBeanInfoResolver
/*    */   implements BeanInfoResolver
/*    */ {
/*    */   public BeanInfo getBeanInfo(Object object)
/*    */   {
/* 65 */     if (object == null) {
/* 66 */       return null;
/*    */     }
/*    */     
/* 69 */     return getBeanInfo(object.getClass());
/*    */   }
/*    */   
/*    */   public BeanInfo getBeanInfo(Class clazz) {
/* 73 */     if (clazz == null) {
/* 74 */       return null;
/*    */     }
/*    */     
/* 77 */     String classname = clazz.getName();
/*    */     
/*    */ 
/* 80 */     int index = classname.indexOf(".impl.basic");
/* 81 */     if ((index != -1) && (classname.endsWith("Basic"))) {
/* 82 */       classname = classname.substring(0, index) + classname.substring(index + ".impl.basic".length(), classname.lastIndexOf("Basic"));
/*    */       
/*    */ 
/*    */ 
/*    */       try
/*    */       {
/* 88 */         return getBeanInfo(Class.forName(classname));
/*    */       } catch (ClassNotFoundException e) {
/* 90 */         return null;
/*    */       }
/*    */     }
/*    */     try {
/* 94 */       return (BeanInfo)Class.forName(classname + "BeanInfo").newInstance();
/*    */     }
/*    */     catch (Exception e) {}
/*    */     
/* 98 */     return null;
/*    */   }
/*    */ }


/* Location:           D:\MyJAVA_JDemetra\1.5.2\nbdemetra\nbdemetra-app\target\nbdemetra\nbdemetra\modules\ext\com.l2fprod.common\l2fprod-common-all-7.3\
 * Qualified Name:     com.l2fprod.common.model.DefaultBeanInfoResolver
 * JD-Core Version:    0.7.0.1
 */