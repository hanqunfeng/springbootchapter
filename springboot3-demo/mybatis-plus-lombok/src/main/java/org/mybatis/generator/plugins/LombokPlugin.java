package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Mybatis Generator Lombok Plugin
 */
public class LombokPlugin extends PluginAdapter {

    /**
     * 是否使用lombok注解
     */
    private boolean hasLombok;
    /**
     * 是否先清除存在的mapper.xml
     */
    private boolean deleteMapperXml;

    /**
     * 是否添加 useGeneratedKeys = true, keyProperty = "id", keyColumn = "id"
     */
    private boolean useGeneratedKeys;

    public LombokPlugin() {
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        hasLombok = Boolean.parseBoolean(properties.getProperty("hasLombok"));
        deleteMapperXml = Boolean.parseBoolean(properties.getProperty("deleteMapperXml"));
        useGeneratedKeys = Boolean.parseBoolean(properties.getProperty("useGeneratedKeys"));
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    /**
     * 为实体添加lombok的注解
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (hasLombok) {
            //添加domain的import
//        topLevelClass.addImportedType("java.io.Serializable");
            topLevelClass.addImportedType("lombok.Data");
//            topLevelClass.addImportedType("lombok.Builder");
//            topLevelClass.addImportedType("lombok.NoArgsConstructor");
//            topLevelClass.addImportedType("lombok.AllArgsConstructor");
            //添加domain的注解
            topLevelClass.addAnnotation("@Data");
//            topLevelClass.addAnnotation("@Builder");
//            topLevelClass.addAnnotation("@NoArgsConstructor");
//            topLevelClass.addAnnotation("@AllArgsConstructor");

            //添加domain的注释
            topLevelClass.addJavaDocLine("/**");
            topLevelClass.addJavaDocLine(" * Created by Mybatis Generator on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            topLevelClass.addJavaDocLine(" */");
            //添加domain的接口
//        topLevelClass.addSuperInterface(new FullyQualifiedJavaType("java.io.Serializable"));
            return true;
        } else {
            return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
        }
    }

    /**
     * 为实体类字段添加注释
     *
     * @param field
     * @param topLevelClass
     * @param introspectedColumn
     * @param introspectedTable
     * @param modelClassType
     * @return
     */
    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        field.addJavaDocLine("/**");
        String remarks = introspectedColumn.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            for (String remarkLine : remarkLines) {
                field.addJavaDocLine(" * " + remarkLine);
            }
        }
        field.addJavaDocLine(" */");
        return true;
    }

    /**
     * mapper class的注释
     *
     * @param interfaze
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, IntrospectedTable introspectedTable) {
        //Mapper文件的注释
        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * Created by Mybatis Generator on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        interfaze.addJavaDocLine(" */");
        return true;
    }

    /**
     * 先删除存在的Mapper.xml
     *
     * @param sqlMap
     * @param introspectedTable
     * @return boolean
     * @author hanqf
     */
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        if (deleteMapperXml) {
            String sqlMapPath = sqlMap.getTargetProject() + File.separator
                    + sqlMap.getTargetPackage().replaceAll("\\.", File.separator)
                    + File.separator + sqlMap.getFileName();
            File sqlMapFile = new File(sqlMapPath);
            sqlMapFile.delete();
            return true;
        } else {
            return super.sqlMapGenerated(sqlMap, introspectedTable);
        }
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (hasLombok) {
            return false;
        } else {
            return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
        }
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (hasLombok) {
            return false;
        } else {
            return super.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
        }
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (useGeneratedKeys && introspectedTable.getPrimaryKeyColumns().size() == 1 && !introspectedTable.requiresXMLGenerator()) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Options"));
            method.addAnnotation("@Options(useGeneratedKeys = true, keyProperty = \""
                    + introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty() + "\", keyColumn = \""
                    + introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName() + "\")");
        }
        return true;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        if (useGeneratedKeys && introspectedTable.getPrimaryKeyColumns().size() == 1 && !introspectedTable.requiresXMLGenerator()) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Options"));
            method.addAnnotation("@Options(useGeneratedKeys = true, keyProperty = \""
                    + introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty() + "\", keyColumn = \""
                    + introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName() + "\")");
        }
        return true;
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (useGeneratedKeys && introspectedTable.getPrimaryKeyColumns().size() == 1) {
            element.addAttribute(new Attribute("useGeneratedKeys", "true"));
            element.addAttribute(new Attribute("keyProperty", introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty()));
            element.addAttribute(new Attribute("keyColumn", introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName()));
        }
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (useGeneratedKeys && introspectedTable.getPrimaryKeyColumns().size() == 1) {
            element.addAttribute(new Attribute("useGeneratedKeys", "true"));
            element.addAttribute(new Attribute("keyProperty", introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty()));
            element.addAttribute(new Attribute("keyColumn", introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName()));
        }
        return true;
    }
}
