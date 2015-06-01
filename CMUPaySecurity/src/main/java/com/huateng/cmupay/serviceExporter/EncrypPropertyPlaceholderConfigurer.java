package com.huateng.cmupay.serviceExporter;

import org.jasypt.commons.CommonUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class EncrypPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    /*
     * Only one of these instances will be initialized, the other one will be
     * null.
     */
    private final StringEncryptor stringEncryptor;
    private final TextEncryptor   textEncryptor;

    /**
     * <p>
     * Creates an <tt>EncryptablePropertyPlaceholderConfigurer</tt> instance
     * which will use the passed {@link StringEncryptor} object to decrypt
     * encrypted values.
     * </p>
     * 
     * @param stringEncryptor
     *            the {@link StringEncryptor} to be used do decrypt values. It
     *            can not be null.
     */
    public EncrypPropertyPlaceholderConfigurer(final StringEncryptor stringEncryptor) {
        super();
        CommonUtils.validateNotNull(stringEncryptor, "Encryptor cannot be null");
        this.stringEncryptor = stringEncryptor;
        this.textEncryptor = null;
    }

    /**
     * <p>
     * Creates an <tt>EncryptablePropertyPlaceholderConfigurer</tt> instance
     * which will use the passed {@link TextEncryptor} object to decrypt
     * encrypted values.
     * </p>
     * 
     * @param textEncryptor
     *            the {@link TextEncryptor} to be used do decrypt values. It can
     *            not be null.
     */
    public EncrypPropertyPlaceholderConfigurer(final TextEncryptor textEncryptor) {
        super();
        CommonUtils.validateNotNull(textEncryptor, "Encryptor cannot be null");
        this.stringEncryptor = null;
        this.textEncryptor = textEncryptor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.config.PropertyResourceConfigurer#
     * convertPropertyValue(java.lang.String)
     */
    @Override
    protected String convertPropertyValue(final String originalValue) {
        if (!PropertyValueEncryptionUtils.isEncryptedValue(originalValue)) {
            return originalValue;
        }
        if (this.stringEncryptor != null) {
            return PropertyValueEncryptionUtils.decrypt(originalValue, this.stringEncryptor);

        }
        return PropertyValueEncryptionUtils.decrypt(originalValue, this.textEncryptor);
    }

    /*
     * (non-Javadoc)
     * 
     * @since 1.8
     * 
     * @see
     * org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
     * #resolveSystemProperty(java.lang.String)
     */
    @Override
    protected String resolveSystemProperty(final String key) {
        return convertPropertyValue(super.resolveSystemProperty(key));
    }


    
    
    public static void main(String[] args) {  
        //PBEWithMD5AndDES  
//        BasicTextEncryptor encryptor = new BasicTextEncryptor();  
//        encryptor.setPassword("upay");  
//        String encrypted = encryptor.encrypt("cmsz@567");  
//        System.out.println(encrypted);  
        
      StandardPBEStringEncryptor encryptor1 = new StandardPBEStringEncryptor();
      
      encryptor1.setAlgorithm("PBEWithMD5AndDES");
      
      encryptor1.setPassword("upay");
      
      String s = PropertyValueEncryptionUtils.encrypt("cmsz@567", encryptor1);
      System.out.println(s);
    }  
    
}
