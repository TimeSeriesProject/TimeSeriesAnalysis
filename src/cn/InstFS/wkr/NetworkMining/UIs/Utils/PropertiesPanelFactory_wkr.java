/*   1:    */ package cn.InstFS.wkr.NetworkMining.UIs.Utils;
/*   2:    */ 
/*   3:    */ import com.google.common.collect.Iterables;
import com.l2fprod.common.beans.editor.ComboBoxPropertyEditor;
/*   4:    */ import com.l2fprod.common.propertysheet.DefaultProperty;
/*   5:    */ import com.l2fprod.common.propertysheet.Property;
/*   6:    */ import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
/*   7:    */ import com.l2fprod.common.propertysheet.PropertySheetPanel;
/*   8:    */ import com.l2fprod.common.propertysheet.PropertySheetTable;
/*   9:    */ import com.l2fprod.common.propertysheet.PropertySheetTableModel;

import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import ec.nbdemetra.ui.properties.l2fprod.CustomPropertyRendererFactory;
/*  10:    */ import ec.tstoolkit.descriptors.EnhancedPropertyDescriptor;
/*  11:    */ import ec.tstoolkit.descriptors.IPropertyDescriptors;
/*  12:    */ import java.awt.Dimension;
/*  13:    */ import java.beans.BeanInfo;
/*  14:    */ import java.beans.IntrospectionException;
/*  15:    */ import java.beans.Introspector;
/*  16:    */ import java.beans.PropertyChangeEvent;
/*  17:    */ import java.beans.PropertyChangeListener;
/*  18:    */ import java.beans.PropertyDescriptor;
/*  19:    */ import java.lang.reflect.InvocationTargetException;
/*  20:    */ import java.lang.reflect.Method;
/*  21:    */ import java.util.ArrayList;
/*  22:    */ import java.util.List;
/*  23:    */ import javax.swing.JOptionPane;
/*  24:    */ import org.slf4j.Logger;
/*  25:    */ import org.slf4j.LoggerFactory;
/*  26:    */ 
/*  27:    */ 
/*  28:    */ 
/*  29:    */ 
/*  30:    */ 
/*  31:    */ public enum PropertiesPanelFactory_wkr
/*  32:    */ {
/*  33: 33 */   INSTANCE;
/*  34: 34 */   private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesPanelFactory_wkr.class);
/*  35:    */   
/*  36:    */   public PropertySheetPanel createPanel(Object o) {
/*  37: 37 */     return createPanel(o, null);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void update(PropertySheetPanel psp, final Object o, PropertyChangeListener listener) {
/*  41: 41 */     final PropertySheetTableModel model = new PropertySheetTableModel();
/*  42:    */     
/*  43: 43 */     psp.setTable(new PropertySheetTable(model));
/*  44: 44 */     if (o != null) {
/*  45: 45 */       model.setProperties(createProperties(o));
/*  46: 46 */       if (listener != null) {
/*  47: 47 */         model.addPropertyChangeListener(listener);
/*  48:    */       }
/*  49: 49 */       model.addPropertyChangeListener(new PropertyChangeListener()
/*  50:    */       {
/*  51:    */         public void propertyChange(PropertyChangeEvent evt) {
/*  52:    */           try {
/*  53: 53 */             model.setProperties(createProperties(o));
/*  54:    */           } catch (RuntimeException err) {
/*  55: 55 */             String str = err.getMessage();
/*  56:    */           } finally {
/*  57: 57 */             model.fireTableStructureChanged();
/*  58:    */           }
/*  59:    */         }
/*  60:    */       });
/*  61:    */     }
/*  62: 62 */     psp.setToolBarVisible(false);
/*  63: 63 */     psp.setEditorFactory(CustomPropertyEditorRegistry.INSTANCE.getRegistry());
/*  64: 64 */     psp.setRendererFactory(CustomPropertyRendererFactory.INSTANCE.getRegistry());
/*  65: 65 */     psp.setDescriptionVisible(false);
psp.setDescriptionVisible(true);
/*  66: 66 */     psp.setMode(1);
/*  67: 67 */     psp.setSorting(false);
/*  68: 68 */     psp.setPreferredSize(new Dimension(300, 400));
/*  69: 69 */     psp.setRestoreToggleStates(true);

//					psp.setSorting(true);
//					psp.setSortingCategories(true);
//					psp.setSortingProperties(true);
//					psp.setToolBarVisible(true);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public PropertySheetPanel createPanel(final Object o, PropertyChangeListener listener)
/*  73:    */   {
/*  74: 74 */     final PropertySheetTableModel model = new PropertySheetTableModel();
/*  75: 75 */     final PropertySheetPanel psp = new PropertySheetPanel();
/*  76:    */     
/*  77: 77 */     psp.setTable(new PropertySheetTable(model));
/*  78: 78 */     if (o != null) {
/*  79: 79 */       model.setProperties(createProperties(o));
/*  80: 80 */       if (listener != null) {
/*  81: 81 */         model.addPropertyChangeListener(listener);
/*  82:    */       }
/*  83: 83 */       model.addPropertyChangeListener(new PropertyChangeListener()
/*  84:    */       {
/*  85:    */         public void propertyChange(PropertyChangeEvent evt) {
/*  86:    */           try {
/*  87: 87 */             model.setProperties(createProperties(o));
/*  88:    */           } catch (RuntimeException err) {
/*  89: 89 */             String str = err.getMessage();
/*  90:    */           }
/*  91:    */         }
/*  92:    */       });
/*  93:    */     }
/*  94:    */     
/*  95:    */ 
/*  96:    */ 
/*  97: 97 */     psp.setToolBarVisible(false);
/*  98: 98 */     psp.setEditorFactory(CustomPropertyEditorRegistry.INSTANCE.getRegistry());
/*  99: 99 */     psp.setRendererFactory(CustomPropertyRendererFactory.INSTANCE.getRegistry());
/* 100:100 */     psp.setDescriptionVisible(true);
/* 101:101 */     psp.setMode(1);
/* 102:102 */     psp.setSorting(false);
/* 103:103 */     psp.setPreferredSize(new Dimension(300, 400));
/* 104:104 */     psp.setRestoreToggleStates(true);
/* 105:    */     
//				  psp.setSorting(true);
//				  psp.setSortingCategories(true);
//				  psp.setSortingProperties(true);
/* 106:106 */     return psp;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public Property[] createProperties(Object o) {
/* 110:110 */     List<Property> result = new ArrayList();
/* 111:    */     
/* 112:    */ 
/* 113:113 */     if ((o instanceof IPropertyDescriptors)) {
///* 114:114 */       createRoots((IPropertyDescriptors)o, result);
/* 115:    */       
/* 116:    */ 
///* 117:117 */       if (result.isEmpty()) {
/* 118:118 */         createRootProperties((IPropertyDescriptors)o, result, ((IPropertyDescriptors)o).getDisplayName());
///* 119:    */       }
/* 120:    */     }
/* 121:    */     else {
/* 122:    */       try {
/* 123:123 */         BeanInfo info = Introspector.getBeanInfo(o.getClass());
/* 124:124 */         if (info != null) {
/* 125:125 */           createRootProperties(o, info.getPropertyDescriptors(), result);
/* 126:    */         }
/* 127:    */       } catch (IntrospectionException ex) {
/* 128:128 */         LOGGER.error("", ex);
/* 129:    */       }
/* 130:    */     }
/* 131:131 */     return (Property[])Iterables.toArray(result, Property.class);
/* 132:    */   }
/* 133:    */   
/* 134:    */   private void createRoots(IPropertyDescriptors iprops, List<Property> props) {
/* 135:135 */     List<EnhancedPropertyDescriptor> eprops = iprops.getProperties();
/* 136:136 */     for (EnhancedPropertyDescriptor epd : eprops) {
/* 137:    */       try {
/* 138:138 */         Object inner = epd.getDescriptor().getReadMethod().invoke(iprops, new Object[0]);
/* 139:139 */         if ((inner instanceof IPropertyDescriptors)) {
/* 140:140 */           createRootProperties((IPropertyDescriptors)inner, props, epd.getDescriptor().getDisplayName());
/* 141:    */         }
/* 142:    */       } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException ex) {
/* 143:143 */         LOGGER.error("", ex);
/* 144:    */       }
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   private void createRootProperties(IPropertyDescriptors iprops, List<Property> props, String category)
/* 149:    */   {
/* 150:150 */     List<EnhancedPropertyDescriptor> eprops = iprops.getProperties();
/* 151:151 */     for (EnhancedPropertyDescriptor epd : eprops) {
/* 152:    */       try {
/* 153:153 */         Object inner = epd.getDescriptor().getReadMethod().invoke(iprops, new Object[0]);
/* 154:154 */         DefaultProperty root = createProperty(iprops, inner, epd);
/* 155:155 */         if ((inner instanceof IPropertyDescriptors)) {
/* 156:156 */           createProperties((IPropertyDescriptors)inner, root);
/* 157:    */         }
/* 158:158 */         root.setCategory(category);
/* 159:159 */         props.add(root);
/* 160:    */       } catch (Exception ex) {
/* 161:161 */         LOGGER.error("", ex);
/* 162:    */       }
/* 163:    */     }
/* 164:    */   }
/* 165:    */   
/* 166:    */   private void createProperties(IPropertyDescriptors desc, DefaultProperty parent)
/* 167:    */   {
/* 168:168 */     List<EnhancedPropertyDescriptor> props = desc.getProperties();
/* 169:169 */     for (EnhancedPropertyDescriptor epd : props) {
/* 170:    */       try {
/* 171:171 */         Object inner = epd.getDescriptor().getReadMethod().invoke(desc, new Object[0]);
/* 172:172 */         DefaultProperty subProp = createProperty(desc, inner, epd);
/* 173:173 */         parent.addSubProperty(subProp);
/* 174:174 */         if ((inner instanceof IPropertyDescriptors)) {
/* 175:175 */           createProperties((IPropertyDescriptors)inner, subProp);
/* 176:    */         }

/* 177:    */       } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException ex) {
/* 178:178 */         LOGGER.error("", ex);
/* 179:    */       }
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   private DefaultProperty createProperty(final Object owner, Object value, final EnhancedPropertyDescriptor prop) {
/* 184:184 */     DefaultProperty p = new DefaultProperty();
/* 185:    */     try
/* 186:    */     {
/* 187:187 */       PropertyDescriptor propDesc = prop.getDescriptor();
/* 188:188 */       p.setName(propDesc.getName());
/* 189:189 */       p.setDisplayName(propDesc.getDisplayName());
/* 190:190 */       p.setShortDescription(propDesc.getShortDescription());
/* 191:191 */       p.setEditable(!prop.isReadOnly());
/* 192:192 */       p.setCategory("");
/* 193:193 */       p.setType(propDesc.getPropertyType());
/* 194:    */       
/* 195:195 */       if (value != null) {
/* 196:196 */         p.setValue(value);
/* 197:197 */         if (CustomPropertyEditorRegistry.INSTANCE.getRegistry().getEditor(value.getClass()) != null)
/* 198:    */         {
/* 199:199 */           if (p.isEditable()) {
/* 200:200 */             p.addPropertyChangeListener(new PropertyChangeListener()
/* 201:    */             {
/* 202:    */               public void propertyChange(PropertyChangeEvent evt) {
/* 203:    */                 try {
/* 204:204 */                   if (evt.getNewValue() == null) {
/* 205:205 */                     return;
/* 206:    */                   }
/* 207:207 */                   prop.getDescriptor().getWriteMethod().invoke(owner, new Object[] { evt.getNewValue() });
/* 208:208 */                   prop.getRefreshMode();
/* 209:    */ 
/* 210:    */                 }
/* 211:    */                 catch (IllegalAccessException localIllegalAccessException) {}catch (IllegalArgumentException localIllegalArgumentException) {}catch (InvocationTargetException ex)
/* 212:    */                 {
/* 213:213 */                   JOptionPane.showMessageDialog(null, ex.getCause().getMessage());
/* 214:    */                 } catch (RuntimeException err) {
/* 215:215 */                   JOptionPane.showMessageDialog(null, err.getMessage());
/* 216:    */                 }
/* 217:    */               }
/* 218:    */             });
/* 219:    */           }
/* 220:220 */           if (propDesc.getPropertyType().isArray())
/* 221:    */           {
/* 222:222 */             Object[] array = (Object[])value;
/* 223:223 */             p.clearSubProperties();
/* 224:224 */             if (array.length > 0) {
/* 225:225 */               Property[] sp = new Property[array.length];
/* 226:    */               
/* 227:227 */               for (int i = 0; i < array.length; i++) {
/* 228:228 */                 Object element = array[i];
/* 229:229 */                 DefaultProperty subProp = new DefaultProperty();
/* 230:230 */                 subProp.setDisplayName("" + i + 1);
/* 231:231 */                 subProp.setValue(element);
/* 232:232 */                 subProp.setEditable(false);
/* 233:233 */                 sp[i] = subProp;
/* 234:    */               }
/* 235:235 */               p.addSubProperties(sp);
/* 236:    */             }
/* 237:    */           }
/* 238:    */         }
/* 239:    */       }
/* 240:240 */       return p;
/* 241:    */     } catch (Exception err) {}
/* 242:242 */     return null;
/* 243:    */   }
/* 244:    */   
/* 245:    */   private void createRootProperties(Object o, PropertyDescriptor[] eprops, List<Property> props)
/* 246:    */   {
/* 247:247 */     for (PropertyDescriptor pd : eprops) {
/* 248:    */       try {
/* 249:249 */         Object inner = pd.getReadMethod().invoke(o, new Object[0]);
/* 250:250 */         DefaultProperty root = createProperty(o, inner, new EnhancedPropertyDescriptor(pd, 0));
/* 251:251 */         if ((inner instanceof IPropertyDescriptors)) {
/* 252:252 */           createProperties((IPropertyDescriptors)inner, root);
/* 253:    */         }
/* 254:254 */         props.add(root);
/* 255:    */       } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException ex) {
/* 256:256 */         LOGGER.error("", ex);
/* 257:    */       }
/* 258:    */     }
/* 259:    */   }


	public void registerEnumEditor(Class<? extends Enum<?>> type) {
		
		PropertyEditorRegistry m_registry = CustomPropertyEditorRegistry.INSTANCE.getRegistry();
		Enum[] enumConstants = (Enum[]) type.getEnumConstants();
		ComboBoxPropertyEditor.Value[] values = new ComboBoxPropertyEditor.Value[enumConstants.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = new ComboBoxPropertyEditor.Value(enumConstants[i],
					enumConstants[i].toString());
		}

		ComboBoxPropertyEditor editor = new ComboBoxPropertyEditor();
		editor.setAvailableValues(values);
		m_registry.registerEditor(type, editor);
	}
}
