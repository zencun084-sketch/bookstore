package com.bookstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    /**
     * 分页查询图书(含分类名称)
     */
    IPage<Book> selectBookPage(Page<Book> page, @Param("keyword") String keyword,
                               @Param("categoryId") Long categoryId,
                               @Param("minPrice") java.math.BigDecimal minPrice,
                               @Param("maxPrice") java.math.BigDecimal maxPrice,
                               @Param("sortField") String sortField,
                               @Param("sortOrder") String sortOrder);

    /**
     * 热门图书
     */
    List<Book> selectHotBooks(@Param("limit") int limit);

    /**
     * 新书推荐
     */
    List<Book> selectNewBooks(@Param("limit") int limit);

    /**
     * 增加销量
     */
    int addSales(@Param("bookId") Long bookId, @Param("quantity") int quantity);

    /**
     * 减少库存
     */
    int reduceStock(@Param("bookId") Long bookId, @Param("quantity") int quantity);

    /**
     * 各分类图书数量及销量分布
     */
    List<Map<String, Object>> selectCategoryDistribution();
}
