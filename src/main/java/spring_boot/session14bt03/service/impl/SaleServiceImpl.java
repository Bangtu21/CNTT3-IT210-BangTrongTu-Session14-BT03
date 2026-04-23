package spring_boot.session14bt03.service.impl;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring_boot.session14bt03.model.entity.Order;
import spring_boot.session14bt03.model.entity.Product;
import spring_boot.session14bt03.service.SaleService;

@Service
public class SaleServiceImpl implements SaleService {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @Transactional
    public String buyProduct(Long productId) {

        Session session = sessionFactory.getCurrentSession();

        try {
            // Bắt đầu transaction
            // 1. Lấy sản phẩm (Product)
            Product product = session.find(Product.class, productId);

            if (product == null) {
                return "Sản phẩm không tồn tại";
            }

            // 2. Kiểm tra tồn kho(Stock)
            if (product.getStock() <= 0) {
                return "Hết hàng"; // Ném lỗi
            }

            // 3. Trừ kho (Nếu còn)
            product.setStock(product.getStock() - 1);

            // 4. Tạo đơn hàng
            Order order = new Order();
            order.setProductId(productId);
            order.setQuantity(1);

            session.persist(order);

            return "Mua thành công"; //Commit

        } catch (StaleObjectStateException | OptimisticLockException e) {
            return "Hệ thống gặp vấn đề, vui lòng thử lại sau";
        }
    }
}
