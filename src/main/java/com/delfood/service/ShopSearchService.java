package com.delfood.service;

import com.delfood.dto.ShopCategoryDTO;
import com.delfood.dto.ShopDTO;
import com.delfood.mapper.ShopSearchMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShopSearchService {
  @Autowired
  private ShopSearchMapper shopSearchMapper;
  

  public List<ShopCategoryDTO> getCategories() {
    return shopSearchMapper.findAll();
  }
  
  public ShopCategoryDTO geteCategory(Long id) {
    return shopSearchMapper.findById();
  }
  
  public List<ShopDTO> findByCategoryIdAndTownCode(Long categoryId, String townCode) {
    return shopSearchMapper.findByCategoryIdAndTownCode(categoryId, townCode);
  }

}
