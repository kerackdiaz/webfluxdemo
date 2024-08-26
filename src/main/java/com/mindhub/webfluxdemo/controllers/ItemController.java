package com.mindhub.webfluxdemo.controllers;

import com.mindhub.webfluxdemo.models.Item;
import com.mindhub.webfluxdemo.repositories.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/{id}")
    public Mono<Item> getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new ItemNotFoundException(id)));
    }

    @GetMapping
    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @PostMapping
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteItem(@PathVariable Long id) {
        return itemRepository.findById(id)
                .flatMap(item -> itemRepository.delete(item)
                        .then(Mono.just(ResponseEntity.noContent().build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
    class ItemNotFoundException extends RuntimeException {
        public ItemNotFoundException(Long id) {
            super("Item with ID " + id + " not found");
        };
}
