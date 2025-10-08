package com.chobbi.server.services.imp;
import com.chobbi.server.dto.*;
import com.chobbi.server.payload.request.AddToCartRequest;
import com.chobbi.server.entity.*;
import com.chobbi.server.repo.*;
import com.chobbi.server.services.CartServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServicesImp implements CartServices {

    private final ProductRepo productRepo;
    private final AccountRepo accountRepo;
    private final CartRepo cartRepo;
    private final VariationRepo variationRepo;
    private final CartVariationRepo cartVariationRepo;

    @Override
    @Transactional
    public List<CartAddDto> addToCart(AddToCartRequest req) {

        // Kiểm tra account tồn tại
        AccountEntity account = accountRepo.findById(req.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Lấy hoặc tạo mới giỏ hàng
        CartEntity cart = cartRepo.findByAccountEntityId(account.getId())
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setAccountEntity(account);
                    return cartRepo.save(newCart);
                });

        // Lấy product
        ProductEntity product = productRepo.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 4Lấy variation và kiểm tra thuộc cùng product
        VariationEntity variation = variationRepo.findById(req.getVariationId())
                .orElseThrow(() -> new RuntimeException("Variation not found"));

        if (!variation.getProductEntity().getId().equals(product.getId())) {
            throw new RuntimeException("Variation does not belong to the specified product");
        }
        if (variation.getStock() < req.getQuantity()) {
            throw new RuntimeException("Insufficient stock for the requested variation");
        }

        // Kiểm tra variation đã có trong cart chưa
        CartVariationEntity cartVariation = cartVariationRepo
                .findByCartEntityAndVariationEntity(cart, variation)
                .orElse(null);

        if (cartVariation != null) {
            cartVariation.setQuantity(cartVariation.getQuantity() + req.getQuantity());
            cartVariation.setPriceAtTime(variation.getPrice());
        } else {
            cartVariation = new CartVariationEntity();
            cartVariation.setCartEntity(cart);
            cartVariation.setVariationEntity(variation);
            cartVariation.setQuantity(req.getQuantity());
            cartVariation.setPriceAtTime(variation.getPrice());
        }

        cartVariationRepo.save(cartVariation);

        // Trả về toàn bộ sản phẩm trong giỏ
        List<CartVariationEntity> cartItems = cartVariationRepo.findAllByCartEntity(cart);

        return cartItems.stream()
                .map(item -> {
                    VariationEntity var = item.getVariationEntity();
                    ProductEntity prod = var.getProductEntity();
                    return new CartAddDto(
                            prod.getShopEntity().getId(),
                            prod.getId(),
                            prod.getTitle(),
                            var.getPrice()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartDto> getCart(Long accountId) {

        AccountEntity account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        CartEntity cart = cartRepo.findByAccountEntityId(account.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for this account"));

        // Mỗi item trong cart tương ứng 1 sản phẩm (theo variation)
        List<CartDto> cartDtos = new ArrayList<>();

        for (CartVariationEntity item : cartVariationRepo.findAllByCartEntity(cart)) {
            VariationEntity variation = item.getVariationEntity();
            ProductEntity product = variation.getProductEntity();

            CartDto dto = new CartDto();
            dto.setQuantity(item.getQuantity());
            dto.setProductId(product.getId());
            dto.setProductName(product.getTitle());
            dto.setSelected_variation_id(variation.getId());

            // Build tiers (Màu sắc, Kích cỡ,...)
            List<TierDto> tierDtos = product.getTiers().stream().map(tier -> {
                TierDto t = new TierDto();
                t.setId(tier.getId());
                t.setName(tier.getName());
                t.setOptions(
                        tier.getOptionsEntities().stream().map(opt -> {
                            OptionDto o = new OptionDto();
                            o.setId(opt.getId());
                            o.setName(opt.getName());
                            return o;
                        }).collect(Collectors.toList())
                );
                return t;
            }).collect(Collectors.toList());
            dto.setTiers(tierDtos);

            // Build variations list
            List<VariationDto> variationDtos = product.getVariations().stream().map(v -> {
                VariationDto vdto = new VariationDto();
                vdto.setId(v.getId());
                vdto.setSku(v.getSku());
                vdto.setPrice(v.getPrice());
                vdto.setStock(v.getStock());

                // map variation -> option_indices
                List<Integer> indices = new ArrayList<>();
                for (int tierIdx = 0; tierIdx < product.getTiers().size(); tierIdx++) {
                    TierEntity tier = product.getTiers().get(tierIdx);
                    List<OptionsEntity> opts = tier.getOptionsEntities();

                    // tìm option thuộc variation
                    VariationOptionEntity vo = v.getVariationOptionEntityList().stream()
                            .filter(x -> opts.contains(x.getOptionsEntity()))
                            .findFirst().orElse(null);

                    indices.add(vo != null ? opts.indexOf(vo.getOptionsEntity()) : -1);
                }
                vdto.setOption_indices(indices);
                return vdto;
            }).collect(Collectors.toList());
            dto.setVariations(variationDtos);

            cartDtos.add(dto);
        }

        return cartDtos;
    }
}
