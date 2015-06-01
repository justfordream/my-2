package main.java.com.huateng.util;


import java.io.InputStream;

/***********************************/
/*���ߣ������� 
 *ʱ�䣺 09-06-23
 */

public interface XmlDom {
/*********************************************************************/
/*���ܣ�����xml�ĵ�
 * �����������
 *  @rootName:��ڵ����
 *  @path:�����ĵ����·��
 * ����ֵ���� 
 */	
public void createXmlDocment(String rootName,String path);//����xml�ĵ�
/**********************************************************************/
/*���ܣ����Ѿ����ڵ�xml�ĵ�����ӽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @atrbtName:�ڵ��������
 *  @atrbtValue���ڵ�����ֵ
 *  @value���ڵ�����
 */	
public void addNode(String path,String nodeName,String atrbtName,String atrbtValue,String value);//��ӽڵ�
/**********************************************************************/
/*���ܣ����Ѿ����ڵ�xml�ĵ�������½ڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @ndName:���ڵ����
 *  @sonNdNmae:�ӽڵ����
 *  @grandsonndName�����ӽڵ����
 *  @ndatrbtName�����ڵ��������
 *  @ndatrbtVale�����ڵ�����ֵ
 *  @ndText�����ڵ�����
 *  @sonatrbtName���ӽڵ��������
 *  @sonatrbtVale���ӽڵ�����ֵ
 *  @sonndText���ӽڵ�����
 *  @grandsnatrbtName�����ӽڵ��������
 *  @grandsnatrbtVale�����ӽڵ�����ֵ
 *  @grandsnndText�����ӽڵ�����
 */	
public void addNode(String path,String ndName,String sonNdNmae,String grandsonndName,String ndatrbtName,String ndatrbtVale,String ndText,String sonatrbtName,String sonatrbtVale,String sonndText,String grandsnatrbtName,String grandsnatrbtVale,String grandsnndText);

public void addNode(String path,String ndName,String sonNdName1,String sonNdName2,String sonNdName3,String sonNdName4,String ndNameVal,String sonndText1,String sonndText2,String sonndText3,String sonndText4);
/**********************************************************************/
/*���ܣ��ж�xml�ĵ��еĽڵ��Ƿ����
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @atrbtVal������idֵ
 *  @nodeVal���ڵ�����
 * ����ֵ����
 *   @true���ڵ����
 *   @false�������ڷ���
 */		

public boolean isNodeExsitByIdByNdVal(String path,String nodename,String atrbtVal,String nodeVal);
/**********************************************************************/
/*���ܣ��ж�xml�ĵ��еĽڵ��Ƿ����
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @atrbtVal������nameֵ
 *  @nodeVal���ڵ�����
 * ����ֵ����
 *   @true���ڵ����
 *   @false�������ڷ���  
 */	
public boolean isNodeExsitByNameByNdVal(String path,String nodename,String atrbtVal,String nodeVal);
/**********************************************************************/
/*���ܣ��ж�xml�ĵ��еĽڵ��Ƿ����
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @atrbtVal������nameֵ
 *  @nodeVal���ڵ�����
 *  @sonNodeName���ӽڵ����
 *  @sonAtrbtVal���ӽڵ�����nameֵ
 *  @sonNdVal���ӽڵ�����
 */
 
public boolean isNodeExsitByNameBysonNdVal(String path,String nodename,String atrbtVal,String sonNodeName,String sonAtrbtVal,String sonNdVal);
/**********************************************************************/
/*���ܣ��ж�xml�ĵ��еĽڵ��Ƿ����
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @atrbtVal������idֵ
 *  @nodeVal���ڵ�����
 *  @sonNodeName���ӽڵ����
 *  @sonAtrbtVal���ӽڵ�����idֵ
 *  @sonNdVal���ӽڵ����� 
 * ����ֵ����
 *   @true���ڵ����
 *   @false�������ڷ���  
 */
public boolean isNodeExsitByIdBysonNdVal(String path,String nodename,String atrbtVal,String sonNodeName,String sonAtrbtVal,String sonNdVal);
/**********************************************************************/
/*���ܣ�ɾ��xml�ĵ��еĽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @atrbtVal������nameֵ
 *  @value���ڵ�����
 * ����ֵ����
 *   @true���ڵ����
 *   @false�������ڷ���  
 */
public void removeNodeByNmValByNdVal(String path,String nodeName,String atrbtVal,String value);
/**********************************************************************/
/*���ܣ�ɾ��xml�ĵ��еĽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @atrbtVal���ڵ�����idֵ
 *  @value���ڵ�����
 */
public void removeNodeByIdValByNdVal(String path,String nodeName,String atrbtVal,String value);
/**********************************************************************/
/*���ܣ�ɾ��xml�ĵ��еĽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:���ڵ����
 *  @atrbtVal�����ڵ�����nameֵ
 *  @sonText���ӽڵ�����
 */
public void removeNodeByNameBysonVal(String path,String nodeName,String atrbtVal,String sonText);
/**********************************************************************/
/*���ܣ�ɾ��xml�ĵ��еĽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:���ڵ����
 *  @atrbtVal�����ڵ�����Idֵ
 *  @sonText���ӽڵ�����
 */
public void removeNodeByIdBysonVal(String path,String nodeName,String atrbtName,String sonText);
/**********************************************************************/
/*���ܣ�ɾ��xml�ĵ��еĽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @nodeValue:�ڵ�nameֵ
 * 
 */	
public void removeNodeByVal(String path,String nodeName,String nodeValue);
/**********************************************************************/
/*���ܣ�ɾ��xml�ĵ��еĽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @nodeValue:�ڵ�nameֵ
 */	

public void removeNodeByName(String path,String nodeName,String nameValue);
/**********************************************************************/
/*���ܣ�ɾ��xml�ĵ��еĽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @nodeValue:�ڵ�idֵ
 */	

public void removeNodeById(String path,String nodeName,String idvalue);
/**********************************************************************/
/*���ܣ�ɾ��xml�ĵ��еĽڵ�
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 */	
public void removeNode(String path,String nodeName);
/*************************************************************************/
/*���ܣ����xml�ĵ��еĸ���ֵ
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 */	
public String getParentNdVal(String path,String nodeName);
/*���ܣ����xml�ĵ��е��ӽ���ֵ
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 */	
public String geSonNdVal(String path,String nodeName);
/*���ܣ����idֵ���xml�ĵ��е�����ڵ��ֵ(��ڵ�)
 * �����������
 *  @path��xml�ĵ�·��
 *  @sonNdname:�ڵ����
 *  @idval:idֵ
 */	
public String getgrdsonNdVal(String path,String sonNdname,String idval);

/*���ܣ����idֵ���xml�ĵ��е�����ڵ��ֵ(��ڵ�)
 * �����������
 *  @path��xml�ĵ�·��
 *  @sonNdname:�ڵ����
 *  @sonidval:idֵ
 */	
public String getGrdsonNdVal(String path,String sonidval,String gsonNdname);


/*���ܣ����idֵ���xml�ĵ��е�������ڵ��ֵ���ļ��ڵ㣩
 * �����������
 *  @path��xml�ĵ�·��
 *  @nodeName:�ڵ����
 *  @idval��idֵ
 *  @grdsonNode������ڵ�
 */	
public String getGgrdsonNdVal(InputStream path,String sonNdname,String idval,String grdsonNode);






}
