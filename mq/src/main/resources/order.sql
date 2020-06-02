drop table if exists `order`;
create table `order` (
    `id` varchar(100) not null  primary key,
    `name` varchar(20),
    `status` integer
);
INSERT INTO `study`.`order`(`id`, `name`, `status`) VALUES ('0000001', '商品祖先', 0);
