# TrungTamTiengAnh

## Yeu cau
- JDK 21
- MySQL 8+

## Cau hinh database
Chinh trong `src/main/resources/application.properties`:
```
spring.datasource.url=jdbc:mysql://localhost:3306/trungtamtienganh
spring.datasource.username=root
spring.datasource.password=
```

## Tai khoan mac dinh
- Admin: `admin` / `admin123`
- Teacher: `VanA` / `111111`
- Student: `truong` / `123456`


## Chay du an
```
./mvnw spring-boot:run
```
Truy cap: http://localhost:8080

## Chuc nang chinh
- Admin: quan ly lop hoc, khoa hoc, giao vien, hoc vien, duyet dang ky, hoc phi
- Teacher: xem lop, danh sach hoc vien, diem danh, nhap diem
- Student: dang ky lop, xem lop cua toi, xem hoc phi
